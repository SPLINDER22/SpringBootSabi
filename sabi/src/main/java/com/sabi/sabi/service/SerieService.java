package com.sabi.sabi.service;

import com.sabi.sabi.dto.SerieDTO;

import java.util.List;

public interface SerieService {
    List<SerieDTO> getAllSeries();
    List<SerieDTO> getAllActiveSeries();

    SerieDTO getSerieById(long id);

    SerieDTO createSerie(SerieDTO serieDTO);

    SerieDTO updateSerie(long id, SerieDTO serieDTO);

    boolean deleteSerie(long id);

    boolean desactivateSerie(long id);
}
