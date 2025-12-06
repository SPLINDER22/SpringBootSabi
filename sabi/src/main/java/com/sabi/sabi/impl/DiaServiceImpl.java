package com.sabi.sabi.impl;

import com.sabi.sabi.dto.DiaDTO;
import com.sabi.sabi.dto.ProgresoSemanaDTO;
import com.sabi.sabi.entity.Dia;
import com.sabi.sabi.entity.Rutina;
import com.sabi.sabi.entity.Semana;
import com.sabi.sabi.repository.DiaRepository;
import com.sabi.sabi.repository.RutinaRepository;
import com.sabi.sabi.repository.SemanaRepository;
import com.sabi.sabi.service.DiaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DiaServiceImpl implements DiaService {
    @Autowired
    private DiaRepository diaRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SemanaRepository semanaRepository;
    @Autowired
    private RutinaRepository rutinaRepository;
    @Autowired
    private com.sabi.sabi.repository.EjercicioAsignadoRepository ejercicioAsignadoRepository;
    @Autowired
    private com.sabi.sabi.service.EjercicioAsignadoService ejercicioAsignadoService;
    @Autowired
    private com.sabi.sabi.repository.SerieRepository serieRepository;
    @Autowired
    private com.sabi.sabi.service.SerieService serieService;

    @Override
    public List<DiaDTO> getAllDia() {
        List<Dia> dia = diaRepository.findAll();
        return dia.stream()
                .map(diaItem -> modelMapper.map(diaItem, DiaDTO.class))
                .toList();
    }

    @Override
    public List<DiaDTO> getAllActiveDia() {
        List<Dia> dia = diaRepository.findByEstadoTrue();
        return dia.stream()
                .map(diaItem -> modelMapper.map(diaItem, DiaDTO.class))
                .toList();
    }

    @Override
    public List<Dia> getDiasSemana(Long idSemana) {
        List<Dia> dias = diaRepository.getDiasSemana(idSemana);
        return dias.stream().toList();
    }

    @Override
    public DiaDTO getDiaById(long id) {
        Dia dia = diaRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Dia not found with id: " + id));
        return modelMapper.map(dia, DiaDTO.class);
    }

    @Override
    public DiaDTO createDia(DiaDTO diaDTO) {
        if (diaDTO.getIdDia() != null) {
            return updateDia(diaDTO.getIdDia(), diaDTO);
        }
        if (diaDTO.getIdSemana() == null) {
            throw new IllegalArgumentException("Debe indicar la semana a la que pertenece el dia");
        }
        Semana semana = semanaRepository.findById(diaDTO.getIdSemana())
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + diaDTO.getIdSemana()));

        Long nextPos;
        List<Dia> actuales = diaRepository.getDiasSemana(diaDTO.getIdSemana());
        if (diaDTO.getNumeroDia() == null || diaDTO.getNumeroDia() <= 0) {
            nextPos = (long) (actuales.size() + 1);
        } else {
            nextPos = diaDTO.getNumeroDia();
            if (nextPos > actuales.size() + 1) {
                nextPos = (long) (actuales.size() + 1);
            }
            if (nextPos <= actuales.size()) {
                for (Dia d : actuales) {
                    if (d.getNumeroDia() >= nextPos) {
                        d.setNumeroDia(d.getNumeroDia() + 1);
                    }
                }
                diaRepository.saveAll(actuales);
            }
        }

        Dia nuevo = new Dia();
        nuevo.setSemana(semana);
        nuevo.setNumeroDia(nextPos);
        nuevo.setDescripcion(diaDTO.getDescripcion());
        nuevo.setNumeroEjercicios(diaDTO.getNumeroEjercicios() != null ? diaDTO.getNumeroEjercicios() : 0L); // default
        nuevo.setEstado(true);

        nuevo = diaRepository.save(nuevo);

        Long numDias = semana.getNumeroDias();
        if (numDias == null) numDias = 0L;
        semana.setNumeroDias(numDias + 1);
        semanaRepository.save(semana);

        return modelMapper.map(nuevo, DiaDTO.class);
    }

    @Override
    public DiaDTO updateDia(long id, DiaDTO diaDTO) {
        Dia existingDia = diaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dia not found with id: " + id));

        if (diaDTO.getIdSemana() != null && !diaDTO.getIdSemana().equals(existingDia.getSemana().getId())) {
            throw new IllegalArgumentException("No se puede cambiar la semana de un dia usando este método.");
        }

        Long semanaId = existingDia.getSemana().getId();
        List<Dia> dias = diaRepository.getDiasSemana(semanaId);

        Long oldPos = existingDia.getNumeroDia();
        Long newPos = diaDTO.getNumeroDia();
        if (newPos == null) newPos = oldPos;
        if (newPos < 1) newPos = 1L;
        if (newPos > dias.size()) newPos = (long) dias.size();

        existingDia.setDescripcion(diaDTO.getDescripcion());
        if (diaDTO.getNumeroEjercicios() != null) {
            existingDia.setNumeroEjercicios(diaDTO.getNumeroEjercicios());
        }

        if (!newPos.equals(oldPos)) {
            if (newPos < oldPos) {
                for (Dia d : dias) {
                    Long num = d.getNumeroDia();
                    if (!d.getId().equals(existingDia.getId()) && num >= newPos && num < oldPos) {
                        d.setNumeroDia(num + 1);
                    }
                }
                existingDia.setNumeroDia(newPos);
            } else {
                for (Dia d : dias) {
                    Long num = d.getNumeroDia();
                    if (!d.getId().equals(existingDia.getId()) && num <= newPos && num > oldPos) {
                        d.setNumeroDia(num - 1);
                    }
                }
                existingDia.setNumeroDia(newPos);
            }
        }

        diaRepository.saveAll(dias);
        diaRepository.save(existingDia);
        return modelMapper.map(existingDia, DiaDTO.class);
    }

    @Override
    public boolean deleteDia(long id) {
        Dia dia = diaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dia not found with id: " + id));
        Semana semana = dia.getSemana();
        Long posEliminada = dia.getNumeroDia();
        diaRepository.delete(dia);

        if (semana != null) {
            List<Dia> restantes = diaRepository.getDiasSemana(semana.getId());
            for (Dia d : restantes) {
                if (d.getNumeroDia() > posEliminada) {
                    d.setNumeroDia(d.getNumeroDia() - 1);
                }
            }
            diaRepository.saveAll(restantes);
            Long numDias = semana.getNumeroDias();
            if (numDias == null || numDias <= 1) {
                semana.setNumeroDias((long) restantes.size());
            } else {
                semana.setNumeroDias(numDias - 1);
            }
            semanaRepository.save(semana);
        }
        return true;
    }

    @Override
    public boolean desactivateDia(long id) {
        Dia dia = diaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dia not found with id: " + id));
        dia.setEstado(!dia.getEstado());
        diaRepository.save(dia);
        return true;
    }

    @Override
    public DiaDTO toggleChecked(long idDia) {
        Dia dia = diaRepository.findById(idDia)
                .orElseThrow(() -> new RuntimeException("Dia not found with id: " + idDia));
        dia.setEstado(dia.getEstado() == null ? Boolean.TRUE : !dia.getEstado());
        diaRepository.save(dia);
        return modelMapper.map(dia, DiaDTO.class);
    }

    @Override
    public DiaDTO getDiaActual(long idCliente) {
        Optional<Rutina> rutina = rutinaRepository.findActiveByClienteId(idCliente);
        if (rutina.isEmpty()) {
            return null; // No hay rutina activa
        }
        List<Semana> semanas = semanaRepository.getSemanasRutina(rutina.get().getId());
        if (semanas == null || semanas.isEmpty()) {
            return null;
        }
        for (Semana semana : semanas) {
            List<Dia> dias = diaRepository.getDiasSemana(semana.getId());
            if (dias == null) continue;
            for (Dia dia : dias) {
                // Se asume que un día "actual" es aquel cuyo estado es FALSE (no completado)
                if (dia.getEstado() != null && !dia.getEstado()) {
                    return modelMapper.map(dia, DiaDTO.class);
                }
            }
        }
        return null;
    }

    @Override
    public long calcularProgresoRutina(long idCliente) {
        long totalDias = 0;
        long diasCompletados = 0;
        Optional<Rutina> rutina = rutinaRepository.findActiveByClienteId(idCliente);
        if (rutina.isEmpty()) {
            return 0L;
        }
        List<Semana> semanas = semanaRepository.getSemanasRutina(rutina.get().getId());
        if (semanas == null || semanas.isEmpty()) {
            return 0L;
        }
        for (Semana semana : semanas) {
            List<Dia> dias = diaRepository.getDiasSemana(semana.getId());
            if (dias == null) continue;
            for (Dia dia : dias) {
                totalDias ++;
                if (dia.getEstado() != null && dia.getEstado()) {
                    diasCompletados ++;
                }
            }
        }
        if (totalDias == 0) return 0L;
        return Math.round(((double) diasCompletados / (double) totalDias) * 100.0);
    }

    @Override
    public List<ProgresoSemanaDTO> calcularProgresoPorSemana(long idCliente) {
        List<ProgresoSemanaDTO> progresosSemanas = new java.util.ArrayList<>();

        Optional<Rutina> rutina = rutinaRepository.findActiveByClienteId(idCliente);
        if (rutina.isEmpty()) {
            return progresosSemanas; // Lista vacía si no hay rutina activa
        }

        List<Semana> semanas = semanaRepository.getSemanasRutina(rutina.get().getId());
        if (semanas == null || semanas.isEmpty()) {
            return progresosSemanas;
        }

        // Recorrer cada semana y calcular su progreso
        for (Semana semana : semanas) {
            List<Dia> dias = diaRepository.getDiasSemana(semana.getId());
            if (dias == null || dias.isEmpty()) {
                continue;
            }

            long totalDias = dias.size();
            long diasCompletados = 0;

            for (Dia dia : dias) {
                if (dia.getEstado() != null && dia.getEstado()) {
                    diasCompletados++;
                }
            }

            ProgresoSemanaDTO progresoSemana = new ProgresoSemanaDTO();
            progresoSemana.setNumeroSemana(semana.getNumeroSemana());
            progresoSemana.setDiasCompletados(diasCompletados);
            progresoSemana.setTotalDias(totalDias);
            progresoSemana.setNombreSemana(semana.getDescripcion()); // Si la semana tiene nombre/descripción

            progresosSemanas.add(progresoSemana);
        }

        return progresosSemanas;
    }

    @Override
    @Transactional
    public DiaDTO duplicarDia(long id) {
        Dia diaOriginal = diaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dia not found with id: " + id));

        // Crear un nuevo día con los mismos datos
        DiaDTO nuevoDiaDTO = new DiaDTO();
        nuevoDiaDTO.setIdSemana(diaOriginal.getSemana().getId());
        nuevoDiaDTO.setDescripcion(diaOriginal.getDescripcion());
        nuevoDiaDTO.setNumeroEjercicios(0L); // Se actualizará al agregar ejercicios
        // El número de día se asignará automáticamente al final en createDia

        DiaDTO nuevoDia = createDia(nuevoDiaDTO);

        // Duplicar todos los ejercicios del día original al NUEVO día
        List<com.sabi.sabi.entity.EjercicioAsignado> ejerciciosOriginales =
            ejercicioAsignadoRepository.getEjesDia(id);

        for (com.sabi.sabi.entity.EjercicioAsignado ejercicioOriginal : ejerciciosOriginales) {
            // Crear un nuevo ejercicio asignado al NUEVO día
            com.sabi.sabi.dto.EjercicioAsignadoDTO nuevoEjeDTO = new com.sabi.sabi.dto.EjercicioAsignadoDTO();
            nuevoEjeDTO.setIdDia(nuevoDia.getIdDia()); // ¡IMPORTANTE! Asignar al nuevo día
            nuevoEjeDTO.setIdEjercicio(ejercicioOriginal.getEjercicio() != null ? ejercicioOriginal.getEjercicio().getId() : null);
            nuevoEjeDTO.setComentarios(ejercicioOriginal.getComentarios());
            nuevoEjeDTO.setNumeroSeries(0L);

            // Crear el nuevo ejercicio
            com.sabi.sabi.dto.EjercicioAsignadoDTO nuevoEje = ejercicioAsignadoService.createEjercicioAsignado(nuevoEjeDTO);

            // Duplicar todas las series del ejercicio original al nuevo ejercicio
            List<com.sabi.sabi.entity.Serie> seriesOriginales = serieRepository.getSerieEje(ejercicioOriginal.getIdEjercicioAsignado());

            for (com.sabi.sabi.entity.Serie serieOriginal : seriesOriginales) {
                com.sabi.sabi.dto.SerieDTO nuevaSerieDTO = new com.sabi.sabi.dto.SerieDTO();
                nuevaSerieDTO.setIdEjercicioAsignado(nuevoEje.getIdEjercicioAsignado()); // Asignar al nuevo ejercicio
                nuevaSerieDTO.setComentarios(serieOriginal.getComentarios());
                nuevaSerieDTO.setPeso(serieOriginal.getPeso());
                nuevaSerieDTO.setRepeticiones(serieOriginal.getRepeticiones());
                nuevaSerieDTO.setDescanso(serieOriginal.getDescanso());
                nuevaSerieDTO.setIntensidad(serieOriginal.getIntensidad());

                serieService.createSerie(nuevaSerieDTO);
            }
        }

        return nuevoDia;
    }
}
