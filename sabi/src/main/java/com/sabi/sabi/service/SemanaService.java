package com.sabi.sabi.service;

import com.sabi.sabi.dto.SemanaDTO;
import com.sabi.sabi.entity.Semana;

import java.util.List;

public interface SemanaService {
    List<SemanaDTO> getAllSemanas();
    List<SemanaDTO> getAllActiveSemanas();

    List<Semana> getSemanasRutina(Long idRutina);

    SemanaDTO getSemanaById(long id);

    SemanaDTO createSemana(SemanaDTO semanaDTO);

    SemanaDTO updateSemana(long id, SemanaDTO semanaDTO);

    boolean deleteSemana(long id);

    void alterCheck(long id);

    boolean desactivateSemana(long id);

    SemanaDTO duplicarSemana(long id);
}
