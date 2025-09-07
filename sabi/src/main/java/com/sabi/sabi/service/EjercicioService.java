package com.sabi.sabi.service;

import com.sabi.sabi.dto.EjercicioDTO;

import java.util.List;

public interface EjercicioService {
    List<EjercicioDTO> getAllEjercicios();
    List<EjercicioDTO> getAllActiveEjercicios();

    EjercicioDTO getEjercicioById(long id);

    EjercicioDTO createEjercicio(EjercicioDTO ejercicioDTO);

    EjercicioDTO updateEjercicio(long id, EjercicioDTO ejercicioDTO);

    boolean deleteEjercicio(long id);

    boolean desactivateEjercicio(long id);
}
