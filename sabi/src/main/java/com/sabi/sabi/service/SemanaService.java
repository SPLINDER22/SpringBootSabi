package com.sabi.sabi.service;

import com.sabi.sabi.dto.SemanaDTO;

import java.util.List;

public interface SemanaService {
    List<SemanaDTO> getAllSemanas();
    List<SemanaDTO> getAllActiveSemanas();

    SemanaDTO getSemanaById(long id);

    SemanaDTO createSemana(SemanaDTO semanaDTO);

    SemanaDTO updateSemana(long id, SemanaDTO semanaDTO);

    boolean deleteSemana(long id);

    boolean desactivateSemana(long id);
}
