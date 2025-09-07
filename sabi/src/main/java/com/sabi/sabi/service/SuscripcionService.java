package com.sabi.sabi.service;

import com.sabi.sabi.dto.SuscripcionDTO;

import java.util.List;

public interface SuscripcionService {
    List<SuscripcionDTO> getAllSuscripciones();
    List<SuscripcionDTO> getAllActiveSuscripciones();

    SuscripcionDTO getSuscripcionById(long id);

    SuscripcionDTO createSuscripcion(SuscripcionDTO suscripcionDTO);

    SuscripcionDTO updateSuscripcion(long id, SuscripcionDTO suscripcionDTO);

    boolean deleteSuscripcion(long id);

    boolean desactivateSuscripcion(long id);
}
