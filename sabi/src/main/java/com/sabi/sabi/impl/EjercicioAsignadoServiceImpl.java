package com.sabi.sabi.impl;

import com.sabi.sabi.dto.EjercicioAsignadoDTO;
import com.sabi.sabi.entity.Dia;
import com.sabi.sabi.entity.Ejercicio;
import com.sabi.sabi.entity.EjercicioAsignado;
import com.sabi.sabi.repository.DiaRepository;
import com.sabi.sabi.repository.EjercicioAsignadoRepository;
import com.sabi.sabi.repository.EjercicioRepository;
import com.sabi.sabi.service.EjercicioAsignadoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EjercicioAsignadoServiceImpl implements EjercicioAsignadoService {
    @Autowired
    private EjercicioAsignadoRepository ejercicioAsignadoRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private DiaRepository diaRepository;
    @Autowired
    private EjercicioRepository ejercicioRepository;
    @Autowired
    private com.sabi.sabi.repository.RegistroSerieRepository registroSerieRepository;

    private EjercicioAsignadoDTO mapManual(EjercicioAsignado e){
        if(e==null) return null;
        EjercicioAsignadoDTO dto = new EjercicioAsignadoDTO();
        dto.setIdEjercicioAsignado(e.getIdEjercicioAsignado());
        dto.setOrden(e.getOrden());
        dto.setComentarios(e.getComentarios());
        dto.setNumeroSeries(e.getNumeroSeries());
        // Protecciones null-safe antes de acceder a relaciones
        if (e.getEjercicio() != null) {
            dto.setNombreEjercicio(e.getEjercicio().getNombre());
            dto.setIdEjercicio(e.getEjercicio().getId());
        } else {
            dto.setNombreEjercicio(null);
            dto.setIdEjercicio(null);
        }
        if (e.getDia() != null) {
            // La entidad Dia usa el campo 'id'
            dto.setIdDia(e.getDia().getId());
        } else {
            dto.setIdDia(null);
        }
        dto.setUrlVideoCliente(e.getUrlVideoCliente());
        dto.setEstado(e.getEstado());
        return dto;
    }

    @Override
    public List<EjercicioAsignadoDTO> getAllEjercicioAsignados() {
        List<EjercicioAsignado> ejercicioAsignados = ejercicioAsignadoRepository.findAll();
        return ejercicioAsignados.stream().map(this::mapManual).toList();
    }

    @Override
    public List<EjercicioAsignadoDTO> getAllActiveEjercicioAsignados() {
        List<EjercicioAsignado> ejercicioAsignados = ejercicioAsignadoRepository.findByEstadoTrue();
        return ejercicioAsignados.stream().map(this::mapManual).toList();
    }

    @Override
    public List<EjercicioAsignadoDTO> getEjesDia(Long idDia) {
        List<EjercicioAsignado> ejesDia = ejercicioAsignadoRepository.getEjesDia(idDia);
        return ejesDia.stream().map(e -> {
            EjercicioAsignadoDTO dto = mapManual(e);
            // Calcular el estado basándose en si tiene registros de series completadas
            if (e.getSeries() != null && !e.getSeries().isEmpty()) {
                long totalSeries = e.getSeries().size();
                // Contar series que tienen al menos un registro
                long seriesCompletadas = e.getSeries().stream()
                    .filter(s -> {
                        if (s.getId() != null) {
                            // Verificar si esta serie tiene registros en la base de datos
                            return !registroSerieRepository.findBySerie_IdIn(java.util.List.of(s.getId())).isEmpty();
                        }
                        return false;
                    })
                    .count();
                // Si todas las series tienen registros, marcar el ejercicio como completado
                dto.setEstado(totalSeries > 0 && seriesCompletadas == totalSeries);
            } else {
                dto.setEstado(e.getEstado()); // Mantener el estado manual si no hay series
            }
            return dto;
        }).toList();
    }

    @Override
    public EjercicioAsignadoDTO getEjercicioAsignadoById(long id) {
        EjercicioAsignado ejercicioAsignado = ejercicioAsignadoRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("EjercicioAsignado not found with id: " + id));
        return mapManual(ejercicioAsignado);
    }

    @Override
    @Transactional
    public EjercicioAsignadoDTO createEjercicioAsignado(EjercicioAsignadoDTO ejercicioAsignadoDTO) {
        if (ejercicioAsignadoDTO.getIdEjercicioAsignado() != null) {
            return updateEjercicioAsignado(ejercicioAsignadoDTO.getIdEjercicioAsignado(), ejercicioAsignadoDTO);
        }
        if (ejercicioAsignadoDTO.getIdDia() == null) {
            throw new IllegalArgumentException("Debe indicar el dia al que pertenece el ejercicio");
        }
        if (ejercicioAsignadoDTO.getIdEjercicio() == null) {
            throw new IllegalArgumentException("Debe indicar el ejercicio a asignar");
        }
        Dia dia = diaRepository.findById(ejercicioAsignadoDTO.getIdDia())
                .orElseThrow(() -> new RuntimeException("Dia not found with id: " + ejercicioAsignadoDTO.getIdDia()));
        Ejercicio ejercicio = ejercicioRepository.findById(ejercicioAsignadoDTO.getIdEjercicio())
                .orElseThrow(() -> new RuntimeException("Ejercicio not found with id: " + ejercicioAsignadoDTO.getIdEjercicio()));

        Long nextPos;
        List<EjercicioAsignado> actuales = ejercicioAsignadoRepository.getEjesDia(ejercicioAsignadoDTO.getIdDia());
        if (ejercicioAsignadoDTO.getOrden() == null || ejercicioAsignadoDTO.getOrden() <= 0) {
            nextPos = (long) (actuales.size() + 1);
        } else {
            nextPos = ejercicioAsignadoDTO.getOrden();
            if (nextPos > actuales.size() + 1) {
                nextPos = (long) (actuales.size() + 1);
            }
            if (nextPos <= actuales.size()) {
                for (EjercicioAsignado e : actuales) {
                    if (e.getOrden() >= nextPos) {
                        e.setOrden(e.getOrden() + 1);
                    }
                }
                ejercicioAsignadoRepository.saveAll(actuales);
            }
        }

        EjercicioAsignado nuevo = new EjercicioAsignado();
        nuevo.setDia(dia);
        nuevo.setEjercicio(ejercicio);
        nuevo.setOrden(nextPos);
        nuevo.setComentarios(ejercicioAsignadoDTO.getComentarios());
        nuevo.setNumeroSeries(ejercicioAsignadoDTO.getNumeroSeries() != null ? ejercicioAsignadoDTO.getNumeroSeries() : 0L);
        nuevo.setEstado(true);

        nuevo = ejercicioAsignadoRepository.save(nuevo);

        Long numEjes = dia.getNumeroEjercicios();
        if (numEjes == null) numEjes = 0L;
        dia.setNumeroEjercicios(numEjes + 1);
        diaRepository.save(dia);

        return mapManual(nuevo);
    }

    @Override
    @Transactional
    public EjercicioAsignadoDTO updateEjercicioAsignado(long id, EjercicioAsignadoDTO ejercicioAsignadoDTO) {
        EjercicioAsignado existingEje = ejercicioAsignadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ejercicio not found with id: " + id));

        if (ejercicioAsignadoDTO.getIdDia() != null && !ejercicioAsignadoDTO.getIdDia().equals(existingEje.getDia().getId())) {
            throw new IllegalArgumentException("No se puede cambiar el dia de un ejercicio usando este método.");
        }

        Long diaId = existingEje.getDia().getId();
        List<EjercicioAsignado> ejes = ejercicioAsignadoRepository.getEjesDia(diaId);

        Long oldPos = existingEje.getOrden();
        Long newPos = ejercicioAsignadoDTO.getOrden();
        if (newPos == null) newPos = oldPos;
        if (newPos < 1) newPos = 1L;
        if (newPos > ejes.size()) newPos = (long) ejes.size();

        existingEje.setComentarios(ejercicioAsignadoDTO.getComentarios());
        if (ejercicioAsignadoDTO.getNumeroSeries() != null) {
            existingEje.setNumeroSeries(ejercicioAsignadoDTO.getNumeroSeries());
        }

        if (!newPos.equals(oldPos)) {
            if (newPos < oldPos) {
                for (EjercicioAsignado e : ejes) {
                    Long num = e.getOrden();
                    if (!e.getIdEjercicioAsignado().equals(existingEje.getIdEjercicioAsignado()) && num >= newPos && num < oldPos) {
                        e.setOrden(num + 1);
                    }
                }
                existingEje.setOrden(newPos);
            } else {
                for (EjercicioAsignado e : ejes) {
                    Long num = e.getOrden();
                    if (!e.getIdEjercicioAsignado().equals(existingEje.getIdEjercicioAsignado()) && num <= newPos && num > oldPos) {
                        e.setOrden(num - 1);
                    }
                }
                existingEje.setOrden(newPos);
            }
        }

        if (ejercicioAsignadoDTO.getIdEjercicio() != null &&
                (existingEje.getEjercicio() == null || !ejercicioAsignadoDTO.getIdEjercicio().equals(existingEje.getEjercicio().getId()))) {
            Ejercicio ejercicio = ejercicioRepository.findById(ejercicioAsignadoDTO.getIdEjercicio())
                    .orElseThrow(() -> new RuntimeException("Ejercicio not found with id: " + ejercicioAsignadoDTO.getIdEjercicio()));
            existingEje.setEjercicio(ejercicio);
        }

        ejercicioAsignadoRepository.saveAll(ejes);
        ejercicioAsignadoRepository.save(existingEje);
        return mapManual(existingEje);
    }

    @Override
    @Transactional
    public boolean deleteEjercicioAsignado(long id) {
        EjercicioAsignado ejercicioAsignado = ejercicioAsignadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ejercicio not found with id: " + id));
        Dia dia = ejercicioAsignado.getDia();
        Long posEliminada = ejercicioAsignado.getOrden();

        ejercicioAsignadoRepository.delete(ejercicioAsignado);

        if (dia != null) {
            List<EjercicioAsignado> restantes = ejercicioAsignadoRepository.getEjesDia(dia.getId());
            for (EjercicioAsignado e : restantes) {
                if (e.getOrden() > posEliminada) {
                    e.setOrden(e.getOrden() - 1);
                }
            }
            ejercicioAsignadoRepository.saveAll(restantes);
            Long numEjes = dia.getNumeroEjercicios();
            if (numEjes == null || numEjes <= 1) {
                dia.setNumeroEjercicios((long) restantes.size());
            } else {
                dia.setNumeroEjercicios(numEjes - 1);
            }
            diaRepository.save(dia);
        }
        return true;
    }

    @Override
    public boolean desactivateEjercicioAsignado(long id) {
        EjercicioAsignado ejercicioAsignado = ejercicioAsignadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EjercicioAsignado not found with id: " + id));
        ejercicioAsignado.setEstado(!ejercicioAsignado.getEstado());
        ejercicioAsignadoRepository.save(ejercicioAsignado);
        return true;
    }

    @Override
    public EjercicioAsignadoDTO toggleChecked(long idEjercicioAsignado) {
        EjercicioAsignado ejercicioAsignado = ejercicioAsignadoRepository.findById(idEjercicioAsignado)
                .orElseThrow(() -> new RuntimeException("EjercicioAsignado not found with id: " + idEjercicioAsignado));
        ejercicioAsignado.setEstado(ejercicioAsignado.getEstado() == null ? Boolean.TRUE : !ejercicioAsignado.getEstado());
        ejercicioAsignadoRepository.save(ejercicioAsignado);
        return mapManual(ejercicioAsignado);
    }
}
