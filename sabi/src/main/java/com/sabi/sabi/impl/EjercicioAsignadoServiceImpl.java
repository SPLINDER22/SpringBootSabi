package com.sabi.sabi.impl;

import com.sabi.sabi.dto.EjercicioAsignadoDTO;
import com.sabi.sabi.entity.EjercicioAsignado;
import com.sabi.sabi.repository.DiaRepository;
import com.sabi.sabi.repository.EjercicioAsignadorRepository;
import com.sabi.sabi.repository.EjercicioRepository;
import com.sabi.sabi.service.EjercicioAsignadoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EjercicioAsignadoServiceImpl implements EjercicioAsignadoService {
    @Autowired
    private EjercicioAsignadorRepository ejercicioAsignadorRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private DiaRepository diaRepository;
    @Autowired
    private EjercicioRepository ejercicioRepository;

    @Override
    public List<EjercicioAsignadoDTO> getAllEjercicioAsignados() {
        List<EjercicioAsignado> ejercicioAsignados = ejercicioAsignadorRepository.findAll();
        return ejercicioAsignados.stream()
                .map(ejercicioAsignado -> modelMapper.map(ejercicioAsignado, EjercicioAsignadoDTO.class))
                .toList();
    }

    @Override
    public List<EjercicioAsignadoDTO> getAllActiveEjercicioAsignados() {
        List<EjercicioAsignado> ejercicioAsignados = ejercicioAsignadorRepository.findByEstadoTrue();
        return ejercicioAsignados.stream()
                .map(ejercicioAsignado -> modelMapper.map(ejercicioAsignado, EjercicioAsignadoDTO.class))
                .toList();
    }

    @Override
    public EjercicioAsignadoDTO getEjercicioAsignadoById(long id) {
        EjercicioAsignado ejercicioAsignado = ejercicioAsignadorRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("EjercicioAsignado not found with id: " + id));
        return modelMapper.map(ejercicioAsignado, EjercicioAsignadoDTO.class);
    }

    @Override
    public EjercicioAsignadoDTO createEjercicioAsignado(EjercicioAsignadoDTO ejercicioAsignadoDTO) {
        EjercicioAsignado ejercicioAsignado = modelMapper.map(ejercicioAsignadoDTO, EjercicioAsignado.class);
        if (ejercicioAsignado.getId() != null && ejercicioAsignadorRepository.findById(ejercicioAsignado.getId()).isPresent()){
            updateEjercicioAsignado(ejercicioAsignado.getId(), ejercicioAsignadoDTO);
        }
        if (ejercicioAsignadoDTO.getIdDia() != null) {
            ejercicioAsignado.setDia(diaRepository.findById(ejercicioAsignadoDTO.getIdDia())
                    .orElseThrow(() -> new RuntimeException("Día not found with id: " + ejercicioAsignadoDTO.getIdDia())));
        }
        if (ejercicioAsignadoDTO.getIdEjercicio() != null) {
            ejercicioAsignado.setEjercicio(ejercicioRepository.findById(ejercicioAsignadoDTO.getIdEjercicio())
                    .orElseThrow(() -> new RuntimeException("Ejercicio not found with id: " + ejercicioAsignadoDTO.getIdEjercicio())));
        }
        ejercicioAsignado = ejercicioAsignadorRepository.save(ejercicioAsignado);
        return modelMapper.map(ejercicioAsignado, EjercicioAsignadoDTO.class);
    }

    @Override
    public EjercicioAsignadoDTO updateEjercicioAsignado(long id, EjercicioAsignadoDTO ejercicioAsignadoDTO) {
        EjercicioAsignado existingEjercicioAsignado = ejercicioAsignadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EjercicioAsignado not found with id: " + id));
        if (ejercicioAsignadoDTO.getIdDia() != null) {
            existingEjercicioAsignado.setDia(diaRepository.findById(ejercicioAsignadoDTO.getIdDia())
                    .orElseThrow(() -> new RuntimeException("Día not found with id: " + ejercicioAsignadoDTO.getIdDia())));
        }
        if (ejercicioAsignadoDTO.getIdEjercicio() != null) {
            existingEjercicioAsignado.setEjercicio(ejercicioRepository.findById(ejercicioAsignadoDTO.getIdEjercicio())
                    .orElseThrow(() -> new RuntimeException("Ejercicio not found with id: " + ejercicioAsignadoDTO.getIdEjercicio())));
        }
        existingEjercicioAsignado.setOrden(ejercicioAsignadoDTO.getOrden());
        existingEjercicioAsignado.setComentarios(ejercicioAsignadoDTO.getComentarios());
        existingEjercicioAsignado.setUrlVideoCliente(ejercicioAsignadoDTO.getUrlVideoCliente());
        existingEjercicioAsignado = ejercicioAsignadorRepository.save(existingEjercicioAsignado);
        return modelMapper.map(existingEjercicioAsignado, EjercicioAsignadoDTO.class);
    }

    @Override
    public boolean deleteEjercicioAsignado(long id) {
        if (!ejercicioAsignadorRepository.findById(id).isPresent()){
            throw new RuntimeException("EjercicioAsignado not found with id: " + id);
        }
        ejercicioAsignadorRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean desactivateEjercicioAsignado(long id) {
        EjercicioAsignado ejercicioAsignado = ejercicioAsignadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EjercicioAsignado not found with id: " + id));
        ejercicioAsignado.setEstado(!ejercicioAsignado.getEstado());
        ejercicioAsignadorRepository.save(ejercicioAsignado);
        return true;
    }
}
