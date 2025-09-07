package com.sabi.sabi.service;

import com.sabi.sabi.dto.EntrenadorDTO;

import java.util.List;

public interface EntrenadorService {
    List<EntrenadorDTO> getEntrenadores();
    List<EntrenadorDTO> getAllActiveEntrenadores();

    EntrenadorDTO getEntrenadorById(long id);

    EntrenadorDTO createEntrenador(EntrenadorDTO entrenadorDTO);

    EntrenadorDTO updateEntrenador(long id, EntrenadorDTO entrenadorDTO);

    boolean deleteEntrenador(long id);

    boolean desactivateEntrenador(long id);
}
