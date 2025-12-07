package com.sabi.sabi.service;

import com.sabi.sabi.dto.SerieDTO;
import com.sabi.sabi.entity.Serie;

import java.util.List;

public interface SerieService {
    List<SerieDTO> getAllSeries();
    List<SerieDTO> getAllActiveSeries();
    List<Serie> getSerieEje(Long idEjercicioAsignado);

    SerieDTO getSerieById(long id);

    SerieDTO createSerie(SerieDTO serieDTO);

    SerieDTO updateSerie(long id, SerieDTO serieDTO);

    boolean deleteSerie(long id);

    boolean desactivateSerie(long id);

    SerieDTO duplicarSerie(long id);
}
