package com.sabi.sabi.impl;

import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Rutina;
import com.sabi.sabi.repository.ClienteRepository;
import com.sabi.sabi.repository.EntrenadorRepository;
import com.sabi.sabi.repository.RutinaRepository;
import com.sabi.sabi.service.RutinaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RutinaServiceImpl implements RutinaService {
    @Autowired
    private RutinaRepository rutinaRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EntrenadorRepository entrenadorRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public List<RutinaDTO> getAllRutinas() {
        List<Rutina> rutinas = rutinaRepository.findAll();
        return rutinas.stream()
                .map(rutina -> modelMapper.map(rutina, RutinaDTO.class))
                .toList();
    }

    @Override
    public List<RutinaDTO> getAllActiveRutinas() {
        List<Rutina> rutinas = rutinaRepository.findByEstadoTrue();
        return rutinas.stream()
                .map(rutina -> modelMapper.map(rutina, RutinaDTO.class))
                .toList();
    }

    @Override
    public List<RutinaDTO> getRutinasPorUsuario(Long usuarioId) {
        List<Rutina> rutinas = rutinaRepository.getRutinasPorUsuario(usuarioId);
        return rutinas.stream()
                .map(r -> modelMapper.map(r, RutinaDTO.class))
                .toList();
    }

    @Override
    public RutinaDTO getRutinaById(long id) {
        Rutina rutina = rutinaRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Rutina not found with id: " + id));
        return modelMapper.map(rutina, RutinaDTO.class);
    }

    @Override
    public RutinaDTO createRutina(RutinaDTO rutinaDTO) {
        Rutina rutina = modelMapper.map(rutinaDTO, Rutina.class);
        if (rutina.getId() != null && rutinaRepository.findById(rutina.getId()).isPresent()){
            updateRutina(rutina.getId(), rutinaDTO);
        }
        if (rutinaDTO.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(rutinaDTO.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente not found with id: " + rutinaDTO.getIdCliente()));
            rutina.setCliente(cliente);
        }
        if (rutinaDTO.getIdEntrenador() != null) {
            Entrenador entrenador = entrenadorRepository.findById(rutinaDTO.getIdEntrenador())
                    .orElseThrow(() -> new RuntimeException("Entrenador not found with id: " + rutinaDTO.getIdEntrenador()));
            rutina.setEntrenador(entrenador);
        }
        rutina = rutinaRepository.save(rutina);
        return modelMapper.map(rutina, RutinaDTO.class);
    }

    @Override
    public RutinaDTO updateRutina(long id, RutinaDTO rutinaDTO) {
        Rutina existingRutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina not found with id: " + id));
        if (rutinaDTO.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(rutinaDTO.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente not found with id: " + rutinaDTO.getIdCliente()));
            existingRutina.setCliente(cliente);
        }
        if (rutinaDTO.getIdEntrenador() != null) {
            Entrenador entrenador = entrenadorRepository.findById(rutinaDTO.getIdEntrenador())
                    .orElseThrow(() -> new RuntimeException("Entrenador not found with id: " + rutinaDTO.getIdEntrenador()));
            existingRutina.setEntrenador(entrenador);
        }
        existingRutina.setNombre(rutinaDTO.getNombre());
        existingRutina.setObjetivo(rutinaDTO.getObjetivo());
        existingRutina.setDescripcion(rutinaDTO.getDescripcion());
        existingRutina.setFechaCreacion(rutinaDTO.getFechaCreacion());
        existingRutina.setEstadoRutina(rutinaDTO.getEstadoRutina());
        existingRutina.setNumeroSemanas(rutinaDTO.getNumeroSemanas());

        existingRutina = rutinaRepository.save(existingRutina);
        return modelMapper.map(existingRutina, RutinaDTO.class);
    }

    @Override
    public boolean deleteRutina(long id) {
        if (!rutinaRepository.findById(id).isPresent()){
            throw new RuntimeException("Rutina not found with id: " + id);
        }
        rutinaRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean desactivateRutina(long id) {
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina not found with id: " + id));
        rutina.setEstado(!rutina.getEstado());
        rutinaRepository.save(rutina);
        return true;
    }
}
