package com.sabi.sabi.service;

import com.sabi.sabi.dto.EjercicioAsignadoDTO;

import java.util.List;

public interface EjercicioAsignadoService {
    List<EjercicioAsignadoDTO> getAllEjercicioAsignados();
    List<EjercicioAsignadoDTO> getAllActiveEjercicioAsignados();

    EjercicioAsignadoDTO getEjercicioAsignadoById(long id);

    EjercicioAsignadoDTO createEjercicioAsignado(EjercicioAsignadoDTO ejercicioAsignadoDTO);

    EjercicioAsignadoDTO updateEjercicioAsignado(long id, EjercicioAsignadoDTO ejercicioAsignadoDTO);

    boolean deleteEjercicioAsignado(long id);

    boolean desactivateEjercicioAsignado(long id);
}
