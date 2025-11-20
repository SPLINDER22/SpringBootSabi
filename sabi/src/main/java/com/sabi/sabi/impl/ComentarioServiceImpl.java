package com.sabi.sabi.impl;

import com.sabi.sabi.dto.ComentarioDTO;
import com.sabi.sabi.entity.*;
import com.sabi.sabi.repository.*;
import com.sabi.sabi.service.ComentarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComentarioServiceImpl implements ComentarioService {
    @Autowired
    private ComentarioRepository comentarioRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EntrenadorRepository entrenadorRepository;
    @Autowired
    private RutinaRepository rutinaRepository;
    @Autowired
    private CalificacionRepository calificacionRepository;
    @Autowired
    private ModelMapper modelMapper;

    private ComentarioDTO mapToDTO(Comentario c){
        if (c == null) return null;
        ComentarioDTO dto = modelMapper.map(c, ComentarioDTO.class);
        dto.setIdComentario(c.getId());
        if (c.getCliente()!=null) dto.setIdCliente(c.getCliente().getId());
        if (c.getEntrenador()!=null) dto.setIdEntrenador(c.getEntrenador().getId());
        if (c.getRutina()!=null) dto.setIdRutina(c.getRutina().getId());
        return dto;
    }

    @Override
    public List<ComentarioDTO> getAllComentarios() {
        return comentarioRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    public List<ComentarioDTO> getAllActiveComentarios() {
        return comentarioRepository.findByEstadoTrue().stream().map(this::mapToDTO).toList();
    }

    @Override
    public List<ComentarioDTO> getComentariosPorEntrenador(Long entrenadorId) {
        return comentarioRepository.findByEntrenadorIdAndEstadoTrue(entrenadorId).stream().map(this::mapToDTO).toList();
    }

    @Override
    public List<ComentarioDTO> getComentariosPorCliente(Long clienteId) {
        return comentarioRepository.findByClienteIdAndEstadoTrue(clienteId).stream().map(this::mapToDTO).toList();
    }

    @Override
    public List<ComentarioDTO> getComentariosPorRutina(Long rutinaId) {
        return comentarioRepository.findByRutinaId(rutinaId).stream().map(this::mapToDTO).toList();
    }

    @Override
    public ComentarioDTO getComentarioById(long id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con id: " + id));
        return mapToDTO(comentario);
    }

    @Override
    @Transactional
    public ComentarioDTO crearComentario(ComentarioDTO dto) {
        if (dto.getCalificacion() == null || dto.getCalificacion() < 0 || dto.getCalificacion() > 5) {
            throw new IllegalArgumentException("La calificacion debe estar entre 0 y 5");
        }
        Comentario c = new Comentario();
        c.setTexto(dto.getTexto());
        c.setCalificacion(dto.getCalificacion());
        c.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + dto.getIdCliente()));
        Entrenador entrenador = entrenadorRepository.findById(dto.getIdEntrenador())
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado: " + dto.getIdEntrenador()));
        c.setCliente(cliente);
        c.setEntrenador(entrenador);
        if (dto.getIdRutina() != null) {
            Rutina r = rutinaRepository.findById(dto.getIdRutina())
                    .orElseThrow(() -> new RuntimeException("Rutina no encontrada: " + dto.getIdRutina()));
            // Validar que no exista ya comentario para esa rutina y cliente
            comentarioRepository.findByClienteIdAndRutinaId(cliente.getId(), r.getId()).ifPresent(x -> {
                throw new RuntimeException("Ya existe un comentario para esta rutina finalizada");
            });
            c.setRutina(r);
        }
        c.setFechaCreacion(LocalDateTime.now());
        c = comentarioRepository.save(c);
        recalcularPromedioEntrenador(entrenador.getId());
        return mapToDTO(c);
    }

    @Override
    @Transactional
    public ComentarioDTO actualizarComentario(long id, ComentarioDTO dto) {
        Comentario c = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con id: " + id));
        if (dto.getTexto()!=null) c.setTexto(dto.getTexto());
        if (dto.getCalificacion()!=null){
            if (dto.getCalificacion() < 0 || dto.getCalificacion() > 5) {
                throw new IllegalArgumentException("La calificacion debe estar entre 0 y 5");
            }
            c.setCalificacion(dto.getCalificacion());
        }
        if (dto.getEstado()!=null) c.setEstado(dto.getEstado());
        c = comentarioRepository.save(c);
        if (c.getEntrenador()!=null) recalcularPromedioEntrenador(c.getEntrenador().getId());
        return mapToDTO(c);
    }

    @Override
    public boolean eliminarComentario(long id) {
        Comentario c = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con id: " + id));
        comentarioRepository.delete(c);
        if (c.getEntrenador()!=null) recalcularPromedioEntrenador(c.getEntrenador().getId());
        return true;
    }

    @Override
    @Transactional
    public boolean cambiarEstado(long id) {
        Comentario c = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con id: " + id));
        c.setEstado(!c.getEstado());
        comentarioRepository.save(c);
        if (c.getEntrenador()!=null) recalcularPromedioEntrenador(c.getEntrenador().getId());
        return true;
    }

    private void recalcularPromedioEntrenador(Long entrenadorId){
        Entrenador e = entrenadorRepository.findById(entrenadorId)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado: " + entrenadorId));
        double suma = 0; int contador = 0;
        for (Calificacion cal : calificacionRepository.findByEstadoTrue()){
            if (cal.getEntrenador()!=null && cal.getEntrenador().getId().equals(entrenadorId)){
                suma += cal.getEstrellas();
                contador++;
            }
        }
        for (Comentario com : comentarioRepository.findByEntrenadorIdAndEstadoTrue(entrenadorId)){
            if (com.getCalificacion()!=null){
                suma += com.getCalificacion();
                contador++;
            }
        }
        e.setCalificacionPromedio(contador==0 ? null : (suma/contador));
        entrenadorRepository.save(e);
    }
}
