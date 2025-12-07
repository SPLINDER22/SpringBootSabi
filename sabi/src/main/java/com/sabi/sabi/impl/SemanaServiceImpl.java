package com.sabi.sabi.impl;

import com.sabi.sabi.dto.SemanaDTO;
import com.sabi.sabi.entity.Rutina;
import com.sabi.sabi.entity.Semana;
import com.sabi.sabi.repository.RutinaRepository;
import com.sabi.sabi.repository.SemanaRepository;
import com.sabi.sabi.service.SemanaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SemanaServiceImpl implements SemanaService {
    @Autowired
    private SemanaRepository semanaRepository;
    @Autowired
    private ModelMapper modelMapper; // aún usado en otros lugares si se requiere
    @Autowired
    private RutinaRepository rutinaRepository;
    @Autowired
    private com.sabi.sabi.repository.DiaRepository diaRepository;
    @Autowired
    private com.sabi.sabi.service.DiaService diaService;
    @Autowired
    private com.sabi.sabi.repository.EjercicioAsignadoRepository ejercicioAsignadoRepository;
    @Autowired
    private com.sabi.sabi.service.EjercicioAsignadoService ejercicioAsignadoService;
    @Autowired
    private com.sabi.sabi.repository.SerieRepository serieRepository;
    @Autowired
    private com.sabi.sabi.service.SerieService serieService;

    private SemanaDTO mapSemana(Semana s){
        if (s == null) return null;
        SemanaDTO dto = new SemanaDTO();
        dto.setIdSemana(s.getId());
        dto.setNumeroSemana(s.getNumeroSemana());
        dto.setDescripcion(s.getDescripcion());
        dto.setNumeroDias(s.getNumeroDias());
        dto.setIdRutina(s.getRutina() != null ? s.getRutina().getId() : null);
        dto.setEstado(s.getEstado());
        return dto;
    }

    @Override
    public List<SemanaDTO> getAllSemanas() {
        return semanaRepository.findAll().stream().map(this::mapSemana).toList();
    }

    @Override
    public List<SemanaDTO> getAllActiveSemanas() {
        return semanaRepository.findByEstadoTrue().stream().map(this::mapSemana).toList();
    }

    @Override
    public List<Semana> getSemanasRutina(Long idRutina) {
        return semanaRepository.getSemanasRutina(idRutina).stream().toList();
    }

    @Override
    public SemanaDTO getSemanaById(long id) {
        Semana semana = semanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + id));
        return mapSemana(semana);
    }

    @Override
    @Transactional
    public SemanaDTO createSemana(SemanaDTO semanaDTO) {
        if (semanaDTO.getIdSemana() != null) {
            throw new IllegalArgumentException("Para actualizar use updateSemana, no createSemana");
        }
        if (semanaDTO.getIdRutina() == null) {
            throw new IllegalArgumentException("Debe indicar la rutina a la que pertenece la semana");
        }
        Rutina rutina = rutinaRepository.findById(semanaDTO.getIdRutina())
                .orElseThrow(() -> new RuntimeException("Rutina not found with id: " + semanaDTO.getIdRutina()));

        Long nextPos;
        List<Semana> actuales = semanaRepository.getSemanasRutina(rutina.getId());
        if (semanaDTO.getNumeroSemana() == null || semanaDTO.getNumeroSemana() <= 0) {
            nextPos = (long) (actuales.size() + 1);
        } else {
            nextPos = semanaDTO.getNumeroSemana();
            if (nextPos > actuales.size() + 1) {
                nextPos = (long) (actuales.size() + 1);
            }
            if (nextPos <= actuales.size()) {
                for (Semana s : actuales) {
                    if (s.getNumeroSemana() >= nextPos) {
                        s.setNumeroSemana(s.getNumeroSemana() + 1);
                    }
                }
                semanaRepository.saveAll(actuales);
            }
        }

        Semana nueva = new Semana();
        nueva.setRutina(rutina);
        nueva.setNumeroSemana(nextPos);
        nueva.setDescripcion(semanaDTO.getDescripcion());
        nueva.setNumeroDias(semanaDTO.getNumeroDias() != null ? semanaDTO.getNumeroDias() : 0L);
        nueva.setEstado(true);

        nueva = semanaRepository.save(nueva);

        Long numSemanas = rutina.getNumeroSemanas();
        if (numSemanas == null) numSemanas = 0L;
        rutina.setNumeroSemanas(numSemanas + 1);
        rutinaRepository.save(rutina);

        return mapSemana(nueva);
    }

    @Override
    @Transactional
    public SemanaDTO updateSemana(long id, SemanaDTO semanaDTO) {
        Semana existingSemana = semanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + id));

        if (semanaDTO.getIdRutina() != null && !semanaDTO.getIdRutina().equals(existingSemana.getRutina().getId())) {
            throw new IllegalArgumentException("No se puede cambiar la rutina de una semana usando este método.");
        }

        Long rutinaId = existingSemana.getRutina().getId();
        List<Semana> semanas = semanaRepository.getSemanasRutina(rutinaId);

        Long oldPos = existingSemana.getNumeroSemana();
        Long newPos = semanaDTO.getNumeroSemana();
        if (newPos == null) newPos = oldPos;
        if (newPos < 1) newPos = 1L;
        if (newPos > semanas.size()) newPos = (long) semanas.size();

        existingSemana.setDescripcion(semanaDTO.getDescripcion());
        if (semanaDTO.getNumeroDias() != null) {
            existingSemana.setNumeroDias(semanaDTO.getNumeroDias());
        }

        if (!newPos.equals(oldPos)) {
            if (newPos < oldPos) {
                for (Semana s : semanas) {
                    Long num = s.getNumeroSemana();
                    if (!s.getId().equals(existingSemana.getId()) && num >= newPos && num < oldPos) {
                        s.setNumeroSemana(num + 1);
                    }
                }
                existingSemana.setNumeroSemana(newPos);
            } else {
                for (Semana s : semanas) {
                    Long num = s.getNumeroSemana();
                    if (!s.getId().equals(existingSemana.getId()) && num <= newPos && num > oldPos) {
                        s.setNumeroSemana(num - 1);
                    }
                }
                existingSemana.setNumeroSemana(newPos);
            }
        }

        semanaRepository.saveAll(semanas);
        semanaRepository.save(existingSemana);
        return mapSemana(existingSemana);
    }

    @Override
    @Transactional
    public boolean deleteSemana(long id) {
        Semana semana = semanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + id));
        Rutina rutina = semana.getRutina();
        Long posEliminada = semana.getNumeroSemana();
        semanaRepository.delete(semana);

        if (rutina != null) {
            List<Semana> restantes = semanaRepository.getSemanasRutina(rutina.getId());
            for (Semana s : restantes) {
                if (s.getNumeroSemana() > posEliminada) {
                    s.setNumeroSemana(s.getNumeroSemana() - 1);
                }
            }
            semanaRepository.saveAll(restantes);
            Long numSemanas = rutina.getNumeroSemanas();
            if (numSemanas == null || numSemanas <= 1) {
                rutina.setNumeroSemanas((long) restantes.size());
            } else {
                rutina.setNumeroSemanas(numSemanas - 1);
            }
            rutinaRepository.save(rutina);
        }
        return true;
    }

    @Override
    public void alterCheck(long id) {
        Semana existingSemana = semanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + id));
        existingSemana.setChecked(!existingSemana.getChecked());
        semanaRepository.save(existingSemana);
    }

    @Override
    public boolean desactivateSemana(long id) {
        Semana existingSemana = semanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + id));
        existingSemana.setEstado(!existingSemana.getEstado());
        semanaRepository.save(existingSemana);
        return true;
    }

    @Override
    @Transactional
    public SemanaDTO duplicarSemana(long id) {
        Semana semanaOriginal = semanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semana not found with id: " + id));

        // Crear una nueva semana con los mismos datos
        SemanaDTO nuevaSemanaDTO = new SemanaDTO();
        nuevaSemanaDTO.setIdRutina(semanaOriginal.getRutina().getId());
        nuevaSemanaDTO.setDescripcion(semanaOriginal.getDescripcion());
        nuevaSemanaDTO.setNumeroDias(0L); // Se actualizará al agregar días
        // El número de semana se asignará automáticamente al final en createSemana

        SemanaDTO nuevaSemana = createSemana(nuevaSemanaDTO);

        // Duplicar todos los días de la semana original a la NUEVA semana
        List<com.sabi.sabi.entity.Dia> diasOriginales = diaRepository.getDiasSemana(id);

        for (com.sabi.sabi.entity.Dia diaOriginal : diasOriginales) {
            // Crear un nuevo día asignado a la NUEVA semana
            com.sabi.sabi.dto.DiaDTO nuevoDiaDTO = new com.sabi.sabi.dto.DiaDTO();
            nuevoDiaDTO.setIdSemana(nuevaSemana.getIdSemana()); // ¡IMPORTANTE! Asignar a la nueva semana
            nuevoDiaDTO.setDescripcion(diaOriginal.getDescripcion());
            nuevoDiaDTO.setNumeroEjercicios(0L);

            // Crear el nuevo día
            com.sabi.sabi.dto.DiaDTO nuevoDia = diaService.createDia(nuevoDiaDTO);

            // Duplicar todos los ejercicios del día original al nuevo día
            List<com.sabi.sabi.entity.EjercicioAsignado> ejerciciosOriginales =
                ejercicioAsignadoRepository.getEjesDia(diaOriginal.getId());

            for (com.sabi.sabi.entity.EjercicioAsignado ejercicioOriginal : ejerciciosOriginales) {
                // Crear un nuevo ejercicio asignado al NUEVO día
                com.sabi.sabi.dto.EjercicioAsignadoDTO nuevoEjeDTO = new com.sabi.sabi.dto.EjercicioAsignadoDTO();
                nuevoEjeDTO.setIdDia(nuevoDia.getIdDia()); // Asignar al nuevo día
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
        }

        return nuevaSemana;
    }
}
