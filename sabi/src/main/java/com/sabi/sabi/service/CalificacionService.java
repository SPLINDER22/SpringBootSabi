package com.sabi.sabi.service;

import com.sabi.sabi.dto.CalificacionDTO;

import java.util.List;

public interface CalificacionService {
    List<CalificacionDTO> getAllCalificaciones();
    List<CalificacionDTO> getAllActiveCalificaciones();

    CalificacionDTO getCalificacionById(long id);

    CalificacionDTO createCalificacion(CalificacionDTO calificacionDTO);

    CalificacionDTO updateCalificacion(long id, CalificacionDTO calificacionDTO);

    boolean deleteCalificacion(long id);

    boolean desactivateCalificacion(long id);
}
