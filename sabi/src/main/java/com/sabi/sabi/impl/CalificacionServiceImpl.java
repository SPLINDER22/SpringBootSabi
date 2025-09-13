package com.sabi.sabi.impl;

import com.sabi.sabi.dto.CalificacionDTO;
import com.sabi.sabi.entity.Calificacion;
import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.repository.CalificacionRepository;
import com.sabi.sabi.repository.ClienteRepository;
import com.sabi.sabi.repository.EntrenadorRepository;
import com.sabi.sabi.service.CalificacionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalificacionServiceImpl implements CalificacionService {
    @Autowired
    private CalificacionRepository calificacionRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EntrenadorRepository entrenadorRepository;

    @Override
    public List<CalificacionDTO> getAllCalificaciones() {
        List<Calificacion> calificaciones = calificacionRepository.findAll();
        return calificaciones.stream()
                .map(calificacion -> modelMapper.map(calificacion, CalificacionDTO.class))
                .toList();
    }

    @Override
    public List<CalificacionDTO> getAllActiveCalificaciones() {
        List<Calificacion> calificaciones = calificacionRepository.findByEstadoTrue();
        return calificaciones.stream()
                .map(calificacion -> modelMapper.map(calificacion, CalificacionDTO.class))
                .toList();
    }

    @Override
    public CalificacionDTO getCalificacionById(long id) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Calificacion no encontrada con id: " + id));
        return modelMapper.map(calificacion, CalificacionDTO.class);
    }

    @Override
    public CalificacionDTO createCalificacion(CalificacionDTO calificacionDTO) {
        Calificacion calificacion = modelMapper.map(calificacionDTO, Calificacion.class);
        if (calificacion.getId() != null && calificacionRepository.findById(calificacion.getId()).isPresent()){
            updateCalificacion(calificacion.getId(), calificacionDTO);
        }

        if (calificacionDTO.getIdCliente() != null){
            Cliente cliente = clienteRepository.findById(calificacionDTO.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + calificacionDTO.getIdCliente()));
            calificacion.setCliente(cliente);
        }
        if (calificacionDTO.getIdEntrenador() != null){
            Entrenador entrenador = entrenadorRepository.findById(calificacionDTO.getIdEntrenador())
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado con id: " + calificacionDTO.getIdEntrenador()));
            calificacion.setEntrenador(entrenador);
        }
        calificacion = calificacionRepository.save(calificacion);
        return modelMapper.map(calificacion, CalificacionDTO.class);
    }

    @Override
    public CalificacionDTO updateCalificacion(long id, CalificacionDTO calificacionDTO) {
        Calificacion existingCalificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calificacion no encontrada con id: " + id));
        if (calificacionDTO.getIdCliente() != null){
            Cliente cliente = clienteRepository.findById(calificacionDTO.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + calificacionDTO.getIdCliente()));
            existingCalificacion.setCliente(cliente);
        }
        if (calificacionDTO.getIdEntrenador() != null){
            Entrenador entrenador = entrenadorRepository.findById(calificacionDTO.getIdEntrenador())
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado con id: " + calificacionDTO.getIdEntrenador()));
            existingCalificacion.setEntrenador(entrenador);
        }
        existingCalificacion.setEstrellas(calificacionDTO.getEstrellas());
        existingCalificacion.setComentario(calificacionDTO.getComentario());
        existingCalificacion = calificacionRepository.save(existingCalificacion);
        return modelMapper.map(existingCalificacion, CalificacionDTO.class);
    }

    @Override
    public boolean deleteCalificacion(long id) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calificacion no encontrada con id: " + id));
        calificacionRepository.delete(calificacion);
        return true;
    }

    @Override
    public boolean desactivateCalificacion(long id) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calificacion no encontrada con id: " + id));
        calificacion.setEstado(!calificacion.getEstado());
        calificacionRepository.save(calificacion);
        return true;
    }
}
