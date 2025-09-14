package com.sabi.sabi.service;

import com.sabi.sabi.dto.EjercicioDTO;

import java.util.List;

public interface EjercicioService {

    List<EjercicioDTO> getEjerciciosPorUsuario(Long usuarioId);

    EjercicioDTO getEjercicioById(long id);

    EjercicioDTO createEjercicio(EjercicioDTO ejercicioDTO, Long usuarioId);

    EjercicioDTO updateEjercicio(long id, EjercicioDTO ejercicioDTO);

    boolean deleteEjercicio(long id);

    boolean desactivateEjercicio(long id);
}
