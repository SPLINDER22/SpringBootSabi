package com.sabi.sabi.impl;

import com.sabi.sabi.dto.CalificacionDTO;
import com.sabi.sabi.service.CalificacionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalificacionServiceImpl implements CalificacionService {

    @Override
    public List<CalificacionDTO> getAllCalificaciones() {
        return List.of();
    }

    @Override
    public List<CalificacionDTO> getAllActiveCalificaciones() {
        return List.of();
    }

    @Override
    public CalificacionDTO getCalificacionById(long id) {
        return null;
    }

    @Override
    public CalificacionDTO createCalificacion(CalificacionDTO calificacionDTO) {
        return null;
    }

    @Override
    public CalificacionDTO updateCalificacion(long id, CalificacionDTO calificacionDTO) {
        return null;
    }

    @Override
    public boolean deleteCalificacion(long id) {
        return false;
    }

    @Override
    public boolean desactivateCalificacion(long id) {
        return false;
    }
}
