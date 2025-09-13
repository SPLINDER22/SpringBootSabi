package com.sabi.sabi.impl;

import com.sabi.sabi.dto.SerieDTO;
import com.sabi.sabi.entity.EjercicioAsignado;
import com.sabi.sabi.entity.Serie;
import com.sabi.sabi.repository.EjercicioAsignadorRepository;
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
    private EjercicioAsignadorRepository ejercicioAsignadorRepository;

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
    public SerieDTO getSerieById(long id) {
        Serie serie = serieRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Serie not found with id: " + id));
        return modelMapper.map(serie, SerieDTO.class);
    }

    @Override
    public SerieDTO createSerie(SerieDTO serieDTO) {
        Serie serie = modelMapper.map(serieDTO, Serie.class);
        if (serie.getId() != null && serieRepository.findById(serie.getId()).isPresent()){
            updateSerie(serie.getId(), serieDTO);
        }
        if (serieDTO.getIdEjercicioAsignado() != null) {
            EjercicioAsignado ejercicioAsignado = ejercicioAsignadorRepository.findById(serieDTO.getIdEjercicioAsignado())
                    .orElseThrow(() -> new RuntimeException("EjercicioAsignado not found with id: " + serieDTO.getIdEjercicioAsignado()));
            serie.setEjercicioAsignado(ejercicioAsignado);
        }
        serie = serieRepository.save(serie);
        return modelMapper.map(serie, SerieDTO.class);
    }

    @Override
    public SerieDTO updateSerie(long id, SerieDTO serieDTO) {
        Serie existingSerie = serieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie not found with id: " + id));
        if (serieDTO.getIdEjercicioAsignado() != null) {
            EjercicioAsignado ejercicioAsignado = ejercicioAsignadorRepository.findById(serieDTO.getIdEjercicioAsignado())
                    .orElseThrow(() -> new RuntimeException("EjercicioAsignado not found with id: " + serieDTO.getIdEjercicioAsignado()));
            existingSerie.setEjercicioAsignado(ejercicioAsignado);
        }
        existingSerie.setOrden(serieDTO.getOrden());
        existingSerie.setRepeticiones(serieDTO.getRepeticiones());
        existingSerie.setPeso(serieDTO.getPeso());
        existingSerie.setDescanso(serieDTO.getDescanso());
        existingSerie.setIntensidad(serieDTO.getIntensidad());
        existingSerie.setComentarios(serieDTO.getComentarios());

        existingSerie = serieRepository.save(existingSerie);
        return modelMapper.map(existingSerie, SerieDTO.class);
    }

    @Override
    public boolean deleteSerie(long id) {
        if (!serieRepository.findById(id).isPresent()){
            throw new RuntimeException("Serie not found with id: " + id);
        }
        serieRepository.deleteById(id);
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
