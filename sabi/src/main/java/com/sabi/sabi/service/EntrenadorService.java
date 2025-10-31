package com.sabi.sabi.service;

import com.sabi.sabi.dto.EntrenadorDTO;

import java.util.List;

public interface EntrenadorService {

    List<EntrenadorDTO> getEntrenadores();

    List<EntrenadorDTO> buscarEntrenadores(String nombre);

    List<EntrenadorDTO> buscarEntrenadores(String nombre, String especialidad, Double minPuntuacion, Double maxPuntuacion);

    List<EntrenadorDTO> getAllActiveEntrenadores();

    EntrenadorDTO getEntrenadorById(long id);

    EntrenadorDTO createEntrenador(EntrenadorDTO entrenadorDTO);

    EntrenadorDTO updateEntrenador(long id, EntrenadorDTO entrenadorDTO);

    void deleteEntrenador(long id);
}
