package com.sabi.sabi.service;

import com.sabi.sabi.dto.EjercicioAsignadoDTO;
import com.sabi.sabi.entity.EjercicioAsignado;

import java.util.List;

public interface EjercicioAsignadoService {
    List<EjercicioAsignadoDTO> getAllEjercicioAsignados();
    List<EjercicioAsignadoDTO> getAllActiveEjercicioAsignados();

    List<EjercicioAsignadoDTO> getEjesDia(Long idDia);

    EjercicioAsignadoDTO getEjercicioAsignadoById(long id);

    EjercicioAsignadoDTO createEjercicioAsignado(EjercicioAsignadoDTO ejercicioAsignadoDTO);

    EjercicioAsignadoDTO updateEjercicioAsignado(long id, EjercicioAsignadoDTO ejercicioAsignadoDTO);

    boolean deleteEjercicioAsignado(long id);

    boolean desactivateEjercicioAsignado(long id);

    EjercicioAsignadoDTO toggleChecked(long idEjercicioAsignado);

    EjercicioAsignadoDTO duplicarEjercicioAsignado(long id);
}
