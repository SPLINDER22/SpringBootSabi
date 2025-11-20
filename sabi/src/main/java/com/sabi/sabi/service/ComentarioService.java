package com.sabi.sabi.service;

import com.sabi.sabi.dto.ComentarioDTO;
import java.util.List;

public interface ComentarioService {
    List<ComentarioDTO> getAllComentarios();
    List<ComentarioDTO> getAllActiveComentarios();
    List<ComentarioDTO> getComentariosPorEntrenador(Long entrenadorId);
    List<ComentarioDTO> getComentariosPorCliente(Long clienteId);
    List<ComentarioDTO> getComentariosPorRutina(Long rutinaId);
    ComentarioDTO getComentarioById(long id);
    ComentarioDTO crearComentario(ComentarioDTO comentarioDTO);
    ComentarioDTO actualizarComentario(long id, ComentarioDTO comentarioDTO);
    boolean eliminarComentario(long id);
    boolean cambiarEstado(long id);
}
