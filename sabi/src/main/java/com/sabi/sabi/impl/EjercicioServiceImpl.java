package com.sabi.sabi.impl;

import com.sabi.sabi.dto.EjercicioDTO;
import com.sabi.sabi.entity.Ejercicio;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.repository.EjercicioRepository;
import com.sabi.sabi.repository.EntrenadorRepository;
import com.sabi.sabi.service.EjercicioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EjercicioServiceImpl implements EjercicioService {
    @Autowired
    private EjercicioRepository ejercicioRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EntrenadorRepository entrenadorRepository;

    @Override
    public List<EjercicioDTO> getAllEjercicios() {
        List<Ejercicio> ejercicios = List.of();
        return ejercicios.stream()
                .map(ejercicio -> new EjercicioDTO())
                .toList();
    }

    @Override
    public List<EjercicioDTO> getAllActiveEjercicios() {
        List<Ejercicio> ejercicios = ejercicioRepository.findByEstadoTrue();
        return ejercicios.stream()
                .map(ejercicio -> modelMapper.map(ejercicio, EjercicioDTO.class))
                .toList();
    }

    @Override
    public EjercicioDTO getEjercicioById(long id) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ejercicio not found with id: " + id));
        return modelMapper.map(ejercicio, EjercicioDTO.class);
    }

    @Override
    public EjercicioDTO createEjercicio(EjercicioDTO ejercicioDTO) {
        Ejercicio ejercicio = modelMapper.map(ejercicioDTO, Ejercicio.class);
        if (ejercicio.getId() != null && ejercicioRepository.findById(ejercicio.getId()).isPresent()){
            updateEjercicio(ejercicio.getId(), ejercicioDTO);
        }
        if (ejercicioDTO.getIdEntrenador() != null) {
            Entrenador entrenador = entrenadorRepository.findById(ejercicioDTO.getIdEntrenador())
                    .orElseThrow(() -> new RuntimeException("Entrenador not found with id: " + ejercicioDTO.getIdEntrenador()));
            ejercicio.setEntrenador(entrenador);
        }
        ejercicio = ejercicioRepository.save(ejercicio);
        return modelMapper.map(ejercicio, EjercicioDTO.class);
    }

    @Override
    public EjercicioDTO updateEjercicio(long id, EjercicioDTO ejercicioDTO) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ejercicio not found with id: " + id));
        if (ejercicioDTO.getIdEntrenador() != null) {
            Entrenador entrenador = entrenadorRepository.findById(ejercicioDTO.getIdEntrenador())
                    .orElseThrow(() -> new RuntimeException("Entrenador not found with id: " + ejercicioDTO.getIdEntrenador()));
            ejercicio.setEntrenador(entrenador);
        }
        ejercicio.setNombre(ejercicioDTO.getNombre());
        ejercicio.setDescripcion(ejercicioDTO.getDescripcion());
        ejercicio.setGrupoMuscular(ejercicioDTO.getGrupoMuscular());
        ejercicio.setEquipo(ejercicioDTO.getEquipo());
        ejercicio.setUrlVideo(ejercicioDTO.getUrlVideo());
        ejercicio.setUrlImagen(ejercicioDTO.getUrlImagen());
        ejercicio.setTipo(ejercicioDTO.getTipo());

        ejercicio = ejercicioRepository.save(ejercicio);
        return modelMapper.map(ejercicio, EjercicioDTO.class);
    }

    @Override
    public boolean deleteEjercicio(long id) {
        if (!ejercicioRepository.findById(id).isPresent()){
            throw new RuntimeException("Ejercicio not found with id: " + id);
        }
        ejercicioRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean desactivateEjercicio(long id) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ejercicio not found with id: " + id));
        ejercicio.setEstado(!ejercicio.getEstado());
        ejercicioRepository.save(ejercicio);
        return true;
    }
}
