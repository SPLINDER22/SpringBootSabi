package com.sabi.sabi.impl;

import com.sabi.sabi.dto.EntrenadorDTO;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.repository.EntrenadorRepository;
import com.sabi.sabi.service.EntrenadorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntrenadorServiceImpl implements EntrenadorService {
    @Autowired
    private EntrenadorRepository entrenadorRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<EntrenadorDTO> getEntrenadores() {
        List<Entrenador> entrenadores = entrenadorRepository.findAll();
        return entrenadores.stream()
                .map(entrenador -> modelMapper.map(entrenador, EntrenadorDTO.class))
                .toList();
    }

    @Override
    public List<EntrenadorDTO> getAllActiveEntrenadores() {
        List<Entrenador> entrenadores = entrenadorRepository.findByEstadoTrue();
        return entrenadores.stream()
                .map(entrenador -> modelMapper.map(entrenador, EntrenadorDTO.class))
                .toList();
    }

    @Override
    public EntrenadorDTO getEntrenadorById(long id) {
        Entrenador entrenador = entrenadorRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Entrenador not found with id: " + id));
        return modelMapper.map(entrenador, EntrenadorDTO.class);
    }

    @Override
    public EntrenadorDTO createEntrenador(EntrenadorDTO entrenadorDTO) {
        Entrenador entrenador = modelMapper.map(entrenadorDTO, Entrenador.class);
        if (entrenador.getId() != null && entrenadorRepository.findById(entrenador.getId()).isPresent()){
            updateEntrenador(entrenador.getId(), entrenadorDTO);
        }
        entrenador = entrenadorRepository.save(entrenador);
        return modelMapper.map(entrenador, EntrenadorDTO.class);
    }

    @Override
    public EntrenadorDTO updateEntrenador(long id, EntrenadorDTO entrenadorDTO) {
        Entrenador existingEntrenador = entrenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrenador not found with id: " + id));

        // --- Actualizar atributos de Usuario (heredados) ---
        if (entrenadorDTO.getNombre() != null) {
            existingEntrenador.setNombre(entrenadorDTO.getNombre());
        }
        if (entrenadorDTO.getApellido() != null) {
            existingEntrenador.setApellido(entrenadorDTO.getApellido());
        }
        if (entrenadorDTO.getEmail() != null) {
            existingEntrenador.setEmail(entrenadorDTO.getEmail());
        }
        if (entrenadorDTO.getSexo() != null) {
            existingEntrenador.setSexo(entrenadorDTO.getSexo());
        }
        if (entrenadorDTO.getFechaNacimiento() != null) {
            existingEntrenador.setFechaNacimiento(entrenadorDTO.getFechaNacimiento());
        }
        if (entrenadorDTO.getDepartamento() != null) {
            existingEntrenador.setDepartamento(entrenadorDTO.getDepartamento());
        }
        if (entrenadorDTO.getCiudad() != null) {
            existingEntrenador.setCiudad(entrenadorDTO.getCiudad());
        }
        if (entrenadorDTO.getTipoDocumento() != null) {
            existingEntrenador.setTipoDocumento(entrenadorDTO.getTipoDocumento());
        }
        if (entrenadorDTO.getNumeroDocumento() != null) {
            existingEntrenador.setNumeroDocumento(entrenadorDTO.getNumeroDocumento());
        }
        if (entrenadorDTO.getTelefono() != null) {
            existingEntrenador.setTelefono(entrenadorDTO.getTelefono());
        }
        if (entrenadorDTO.getFotoPerfilUrl() != null) {
            existingEntrenador.setFotoPerfilUrl(entrenadorDTO.getFotoPerfilUrl());
        }
        if (entrenadorDTO.getDescripcion() != null) {
            existingEntrenador.setDescripcion(entrenadorDTO.getDescripcion());
        }
        if (entrenadorDTO.getEstado() != null) {
            existingEntrenador.setEstado(entrenadorDTO.getEstado());
        }

        // --- Actualizar atributos específicos de Entrenador ---
        if (entrenadorDTO.getEspecialidad() != null) {
            existingEntrenador.setEspecialidad(entrenadorDTO.getEspecialidad());
        }
        if (entrenadorDTO.getCalificacionPromedio() != null) {
            existingEntrenador.setCalificacionPromedio(entrenadorDTO.getCalificacionPromedio());
        }

        existingEntrenador = entrenadorRepository.save(existingEntrenador);
        return modelMapper.map(existingEntrenador, EntrenadorDTO.class);
    }

    @Override
    public void deleteEntrenador(long id) {
        Entrenador existingEntrenador = entrenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrenador not found with id: " + id));
        entrenadorRepository.delete(existingEntrenador);
    }
}
