package com.sabi.sabi.impl;

import com.sabi.sabi.dto.SuscripcionDTO;
import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Suscripcion;
import com.sabi.sabi.entity.enums.EstadoSuscripcion;
import com.sabi.sabi.repository.ClienteRepository;
import com.sabi.sabi.repository.EntrenadorRepository;
import com.sabi.sabi.repository.SuscripcionRepository;
import com.sabi.sabi.service.SuscripcionService;
import com.sabi.sabi.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private EmailService emailService;

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
        // Asegurar que el precio del DTO se refleje
        suscripcion.setPrecio(suscripcionDTO.getPrecio());
        // Duración en semanas
        suscripcion.setDuracionSemanas(suscripcionDTO.getDuracionSemanas());
        // Flag vista diagnostico
        suscripcion.setVistaDiagnostico(suscripcionDTO.getVistaDiagnostico() != null ? suscripcionDTO.getVistaDiagnostico() : false);
        if (suscripcionDTO.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(suscripcionDTO.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente not found with id: " + suscripcionDTO.getIdCliente()));
            suscripcion.setCliente(cliente);
            // Enforce single active subscription for this client
            boolean hasActive = suscripcionRepository.existsByCliente_IdAndEstadoTrueAndEstadoSuscripcionIsNot(cliente.getId(), EstadoSuscripcion.RECHAZADA);
            if (hasActive) {
                throw new RuntimeException("El cliente ya tiene una suscripción activa.");
            }
        }
        if (suscripcionDTO.getIdEntrenador() != null) {
            Entrenador entrenador = entrenadorRepository.findById(suscripcionDTO.getIdEntrenador())
                    .orElseThrow(() -> new RuntimeException("Entrenador not found with id: " + suscripcionDTO.getIdEntrenador()));
            suscripcion.setEntrenador(entrenador);
        }
        suscripcion = suscripcionRepository.save(suscripcion);
        
        // Enviar notificación al entrenador cuando se crea la solicitud
        if (suscripcion.getEntrenador() != null && suscripcion.getCliente() != null) {
            emailService.enviarNotificacionSolicitudSuscripcion(
                suscripcion.getEntrenador().getEmail(),
                suscripcion.getEntrenador().getNombre(),
                suscripcion.getCliente().getNombre()
            );
        }
        
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
        existingSuscripcion.setPrecio(suscripcionDTO.getPrecio());
        existingSuscripcion.setDuracionSemanas(suscripcionDTO.getDuracionSemanas());
        
        // Si el estado cambia a COTIZADA, enviar notificación al cliente
        boolean cambioACotizada = suscripcionDTO.getEstadoSuscripcion() == EstadoSuscripcion.COTIZADA 
                                  && existingSuscripcion.getEstadoSuscripcion() != EstadoSuscripcion.COTIZADA;
        
        existingSuscripcion.setEstadoSuscripcion(suscripcionDTO.getEstadoSuscripcion());
        existingSuscripcion.setVistaDiagnostico(suscripcionDTO.getVistaDiagnostico() != null ? suscripcionDTO.getVistaDiagnostico() : existingSuscripcion.getVistaDiagnostico());

        existingSuscripcion = suscripcionRepository.save(existingSuscripcion);
        
        // Enviar email al cliente cuando se cotiza
        if (cambioACotizada && existingSuscripcion.getPrecio() != null && existingSuscripcion.getDuracionSemanas() != null) {
            emailService.enviarNotificacionCotizacionRecibida(
                existingSuscripcion.getCliente().getEmail(),
                existingSuscripcion.getCliente().getNombre(),
                existingSuscripcion.getEntrenador().getNombre(),
                existingSuscripcion.getPrecio(),
                existingSuscripcion.getDuracionSemanas()
            );
        }
        
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

    @Override
    public SuscripcionDTO getSuscripcionActualByClienteId(Long clienteId) {
        return suscripcionRepository
                .findTopByCliente_IdAndEstadoTrueAndEstadoSuscripcionIsNotOrderByIdDesc(clienteId, EstadoSuscripcion.RECHAZADA)
                .map(s -> modelMapper.map(s, SuscripcionDTO.class))
                .orElse(null);
    }

    @Override
    public List<SuscripcionDTO> getHistorialByClienteId(Long clienteId) {
        return suscripcionRepository
                .findByCliente_IdAndEstadoTrueAndEstadoSuscripcion(clienteId, EstadoSuscripcion.RECHAZADA)
                .stream()
                .map(s -> modelMapper.map(s, SuscripcionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void cancelarSuscripcion(Long id) {
        Suscripcion suscripcion = suscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suscripcion not found with id: " + id));
        suscripcion.setEstadoSuscripcion(EstadoSuscripcion.RECHAZADA);
        suscripcionRepository.save(suscripcion);
    }

    @Override
    public void cancelarSuscripcionPorClienteYEntrenador(Long clienteId, Long entrenadorId) {
        Suscripcion suscripcion = suscripcionRepository.findByCliente_IdAndEntrenador_IdAndEstadoTrue(clienteId, entrenadorId)
                .orElseThrow(() -> new RuntimeException("No se encontró suscripción activa entre el cliente y el entrenador"));
        suscripcion.setEstadoSuscripcion(EstadoSuscripcion.RECHAZADA);
        suscripcionRepository.save(suscripcion);
    }

    @Override
    public boolean existsSuscripcionActivaByClienteId(Long clienteId) {
        return suscripcionRepository.existsByCliente_IdAndEstadoTrueAndEstadoSuscripcionIsNot(clienteId, EstadoSuscripcion.RECHAZADA);
    }

    @Override
    public List<SuscripcionDTO> getSuscripcionesByDuracionSemanas(Integer duracionSemanas) {
        if (duracionSemanas == null) {
            return List.of();
        }
        return suscripcionRepository.findByEstadoTrueAndEstadoSuscripcionAndDuracionSemanas(EstadoSuscripcion.ACEPTADA, duracionSemanas)
                .stream()
                .map(s -> modelMapper.map(s, SuscripcionDTO.class))
                .collect(Collectors.toList());
    }
}
