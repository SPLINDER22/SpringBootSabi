package com.sabi.sabi.impl;

import com.sabi.sabi.dto.SuscripcionDTO;
import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Suscripcion;
import com.sabi.sabi.repository.ClienteRepository;
import com.sabi.sabi.repository.EntrenadorRepository;
import com.sabi.sabi.repository.SuscripcionRepository;
import com.sabi.sabi.service.SuscripcionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuscripcionServiceImpl implements SuscripcionService {
    @Autowired
    private SuscripcionRepository suscripcionRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EntrenadorRepository entrenadorRepository;

    @Override
    public List<SuscripcionDTO> getAllSuscripciones() {
        List<Suscripcion> suscripciones = suscripcionRepository.findAll();
        return suscripciones.stream()
                .map(suscripcion -> modelMapper.map(suscripcion, SuscripcionDTO.class))
                .toList();
    }

    @Override
    public List<SuscripcionDTO> getAllActiveSuscripciones() {
        List<Suscripcion> suscripciones = suscripcionRepository.findByEstadoTrue();
        return suscripciones.stream()
                .map(suscripcion -> modelMapper.map(suscripcion, SuscripcionDTO.class))
                .toList();
    }

    @Override
    public SuscripcionDTO getSuscripcionById(long id) {
        Suscripcion suscripcion = suscripcionRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Suscripcion not found with id: " + id));
        return modelMapper.map(suscripcion, SuscripcionDTO.class);
    }

    @Override
    public SuscripcionDTO createSuscripcion(SuscripcionDTO suscripcionDTO) {
        Suscripcion suscripcion = modelMapper.map(suscripcionDTO, Suscripcion.class);
        if (suscripcion.getId() != null && suscripcionRepository.findById(suscripcion.getId()).isPresent()){
            updateSuscripcion(suscripcion.getId(), suscripcionDTO);
        }
        if (suscripcionDTO.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(suscripcionDTO.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente not found with id: " + suscripcionDTO.getIdCliente()));
            suscripcion.setCliente(cliente);
        }
        if (suscripcionDTO.getIdEntrenador() != null) {
            Entrenador entrenador = entrenadorRepository.findById(suscripcionDTO.getIdEntrenador())
                    .orElseThrow(() -> new RuntimeException("Entrenador not found with id: " + suscripcionDTO.getIdEntrenador()));
            suscripcion.setEntrenador(entrenador);
        }
        suscripcion = suscripcionRepository.save(suscripcion);
        return modelMapper.map(suscripcion, SuscripcionDTO.class);
    }

    @Override
    public SuscripcionDTO updateSuscripcion(long id, SuscripcionDTO suscripcionDTO) {
        Suscripcion existingSuscripcion = suscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suscripcion not found with id: " + id));
        if (suscripcionDTO.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(suscripcionDTO.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente not found with id: " + suscripcionDTO.getIdCliente()));
            existingSuscripcion.setCliente(cliente);
        }
        if (suscripcionDTO.getIdEntrenador() != null) {
            Entrenador entrenador = entrenadorRepository.findById(suscripcionDTO.getIdEntrenador())
                    .orElseThrow(() -> new RuntimeException("Entrenador not found with id: " + suscripcionDTO.getIdEntrenador()));
            existingSuscripcion.setEntrenador(entrenador);
        }
        existingSuscripcion.setPlazo(suscripcionDTO.getPlazo());
        existingSuscripcion.setFechaInicio(suscripcionDTO.getFechaInicio());
        existingSuscripcion.setFechaFin(suscripcionDTO.getFechaFin());
        existingSuscripcion.setEstadoSuscripcion(suscripcionDTO.getEstadoSuscripcion());

        existingSuscripcion = suscripcionRepository.save(existingSuscripcion);
        return modelMapper.map(existingSuscripcion, SuscripcionDTO.class);
    }

    @Override
    public boolean deleteSuscripcion(long id) {
        if (!suscripcionRepository.findById(id).isPresent()){
            throw new RuntimeException("Suscripcion not found with id: " + id);
        }
        suscripcionRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean desactivateSuscripcion(long id) {
        Suscripcion suscripcion = suscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suscripcion not found with id: " + id));
        suscripcion.setEstado(!suscripcion.getEstado());
        suscripcionRepository.save(suscripcion);
        return true;
    }
}
