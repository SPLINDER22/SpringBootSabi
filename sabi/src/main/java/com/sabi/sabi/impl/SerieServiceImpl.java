package com.sabi.sabi.impl;

import com.sabi.sabi.dto.DiaDTO;
import com.sabi.sabi.dto.SerieDTO;
import com.sabi.sabi.entity.Dia;
import com.sabi.sabi.entity.EjercicioAsignado;
import com.sabi.sabi.entity.Semana;
import com.sabi.sabi.entity.Serie;
import com.sabi.sabi.repository.EjercicioAsignadoRepository;
import com.sabi.sabi.repository.SerieRepository;
import com.sabi.sabi.service.SerieService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SerieServiceImpl implements SerieService {
    @Autowired
    private SerieRepository serieRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EjercicioAsignadoRepository ejercicioAsignadoRepository;

    @Override
    public List<SerieDTO> getAllSeries() {
        List<Serie> serie = serieRepository.findAll();
        return serie.stream()
                .map(serie1 -> modelMapper.map(serie1, SerieDTO.class))
                .toList();
    }

    @Override
    public List<SerieDTO> getAllActiveSeries() {
        List<Serie> series = serieRepository.findByEstadoTrue();
        return series.stream()
                .map(serie -> modelMapper.map(serie, SerieDTO.class))
                .toList();
    }

    @Override
    public List<Serie> getSerieEje(Long idEjercicioAsignado) {
        List<Serie> series = serieRepository.getSerieEje(idEjercicioAsignado);
        return series.stream().toList();
    }

    @Override
    public SerieDTO getSerieById(long id) {
        Serie serie = serieRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Serie not found with id: " + id));
        return modelMapper.map(serie, SerieDTO.class);
    }

    @Override
    public SerieDTO createSerie(SerieDTO serieDTO) {
        if (serieDTO.getId() != null) {
            return updateSerie(serieDTO.getId(), serieDTO);
        }
        if (serieDTO.getIdEjercicioAsignado() == null) {
            throw new IllegalArgumentException("Debe indicar el ejercicio al que pertenece la serie");
        }
        EjercicioAsignado eje = ejercicioAsignadoRepository.findById(serieDTO.getIdEjercicioAsignado())
                .orElseThrow(() -> new RuntimeException("ejercicio not found with id: " + serieDTO.getIdEjercicioAsignado()));

        Long nextPos;
        List<Serie> actuales = serieRepository.getSerieEje(serieDTO.getIdEjercicioAsignado());
        if (serieDTO.getOrden() == null || serieDTO.getOrden() <= 0) {
            nextPos = (long) (actuales.size() + 1);
        } else {
            nextPos = serieDTO.getOrden();
            if (nextPos > actuales.size() + 1) {
                nextPos = (long) (actuales.size() + 1);
            }
            if (nextPos <= actuales.size()) {
                for (Serie s : actuales) {
                    if (s.getOrden() >= nextPos) {
                        s.setOrden(s.getOrden() + 1);
                    }
                }
                serieRepository.saveAll(actuales);
            }
        }

        Serie nueva = new Serie();
        nueva.setEjercicioAsignado(eje);
        nueva.setOrden(nextPos);
        nueva.setComentarios(serieDTO.getComentarios());
        nueva.setPeso(serieDTO.getPeso());
        nueva.setRepeticiones(serieDTO.getRepeticiones());
        nueva.setDescanso(serieDTO.getDescanso());
        nueva.setIntensidad(serieDTO.getIntensidad());
        nueva.setEstado(true);

        nueva = serieRepository.save(nueva);

        Long numSeries = eje.getNumeroSeries();
        if (numSeries == null) numSeries = 0L;
        eje.setNumeroSeries(numSeries + 1);
        ejercicioAsignadoRepository.save(eje);

        return modelMapper.map(nueva, SerieDTO.class);
    }

    @Override
    public SerieDTO updateSerie(long id, SerieDTO serieDTO) {
        Serie existingSerie = serieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie not found with id: " + id));

        if (serieDTO.getIdEjercicioAsignado() != null && !serieDTO.getIdEjercicioAsignado().equals(existingSerie.getEjercicioAsignado().getIdEjercicioAsignado())) {
            throw new IllegalArgumentException("No se puede cambiar el ejercicio de una serie usando este método.");
        }

        Long ejeId = existingSerie.getEjercicioAsignado().getIdEjercicioAsignado();
        List<Serie> series = serieRepository.getSerieEje(ejeId);

        Long oldPos = existingSerie.getOrden();
        Long newPos = serieDTO.getOrden();
        if (newPos == null) newPos = oldPos;
        if (newPos < 1) newPos = 1L;
        if (newPos > series.size()) newPos = (long) series.size();

        if (!newPos.equals(oldPos)) {
            if (newPos < oldPos) {
                for (Serie s : series) {
                    Long num = s.getOrden();
                    if (!s.getId().equals(existingSerie.getId()) && num >= newPos && num < oldPos) {
                        s.setOrden(num + 1);
                    }
                }
                existingSerie.setOrden(newPos);
            } else {
                for (Serie s : series) {
                    Long num = s.getOrden();
                    if (!s.getId().equals(existingSerie.getId()) && num <= newPos && num > oldPos) {
                        s.setOrden(num - 1);
                    }
                }
                existingSerie.setOrden(newPos);
            }
        }

        existingSerie.setComentarios(serieDTO.getComentarios());
        existingSerie.setPeso(serieDTO.getPeso());
        existingSerie.setRepeticiones(serieDTO.getRepeticiones());
        existingSerie.setDescanso(serieDTO.getDescanso());
        existingSerie.setIntensidad(serieDTO.getIntensidad());

        serieRepository.saveAll(series);
        serieRepository.save(existingSerie);
        return modelMapper.map(existingSerie, SerieDTO.class);
    }

    @Override
    public boolean deleteSerie(long id) {
        Serie serie = serieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie not found with id: " + id));
        EjercicioAsignado eje = serie.getEjercicioAsignado();
        Long posEliminada = serie.getOrden();
        serieRepository.delete(serie);

        if (eje != null) {
            List<Serie> restantes = serieRepository.getSerieEje(eje.getIdEjercicioAsignado());
            for (Serie s : restantes) {
                if (s.getOrden() > posEliminada) {
                    s.setOrden(s.getOrden() - 1);
                }
            }
            serieRepository.saveAll(restantes);
            Long numSeries = eje.getNumeroSeries();
            if (numSeries == null || numSeries <= 1) {
                eje.setNumeroSeries((long) restantes.size());
            } else {
                eje.setNumeroSeries(numSeries - 1);
            }
            ejercicioAsignadoRepository.save(eje);
        }
        return true;
    }

    @Override
    public boolean desactivateSerie(long id) {
        Serie serie = serieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie not found with id: " + id));
        serie.setEstado(!serie.getEstado());
        serieRepository.save(serie);
        return true;
    }
}
