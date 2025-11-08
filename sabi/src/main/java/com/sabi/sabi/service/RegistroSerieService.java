package com.sabi.sabi.service;

import com.sabi.sabi.dto.RegistroSerieDTO;

import java.util.List;

public interface RegistroSerieService {
    List<RegistroSerieDTO> getAllRegistroSeries();
    List<RegistroSerieDTO> getAllActiveRegistroSeries();

    RegistroSerieDTO getRegistroSerieById(long id);

    RegistroSerieDTO createRegistroSerie(RegistroSerieDTO registroSerieDTO);

    RegistroSerieDTO updateRegistroSerie(long id, RegistroSerieDTO registroSerieDTO);

    boolean deleteRegistroSerie(long id);

    boolean desactivateRegistroSerie(long id);

    RegistroSerieDTO saveOrUpdateRegistroSerie(RegistroSerieDTO registroSerieDTO);

    List<RegistroSerieDTO> getRegistroSeriesBySerieId(Long idSerie);
}
