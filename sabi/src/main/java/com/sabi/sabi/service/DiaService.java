package com.sabi.sabi.service;

import com.sabi.sabi.dto.DiaDTO;

import java.util.List;

public interface DiaService {
    List<DiaDTO> getAllDia();
    List<DiaDTO> getAllActiveDia();

    DiaDTO getDiaById(long id);

    DiaDTO createDia(DiaDTO diaDTO);

    DiaDTO updateDia(long id, DiaDTO diaDTO);

    boolean deleteDia(long id);

    boolean desactivateDia(long id);
}
