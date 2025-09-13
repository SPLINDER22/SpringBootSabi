package com.sabi.sabi.impl;

import com.sabi.sabi.dto.NotificacionDTO;
import com.sabi.sabi.entity.Notificacion;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.repository.ClienteRepository;
import com.sabi.sabi.repository.NotificacionRepository;
import com.sabi.sabi.repository.UsuarioRepository;
import com.sabi.sabi.service.NotificacionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionServiceImpl implements NotificacionService {
    @Autowired
    private NotificacionRepository notificacionRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<NotificacionDTO> getAllNotificaciones() {
        List<Notificacion> notificaciones = notificacionRepository.findAll();
        return notificaciones.stream()
                .map(notificacion -> modelMapper.map(notificacion, NotificacionDTO.class))
                .toList();
    }

    @Override
    public List<NotificacionDTO> getAllActiveNotificaciones() {
        List<Notificacion> notificaciones = notificacionRepository.findByEstadoTrue();
        return notificaciones.stream()
                .map(notificacion -> modelMapper.map(notificacion, NotificacionDTO.class))
                .toList();
    }

    @Override
    public NotificacionDTO getNotificacionById(long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Notificacion not found with id: " + id));
        return modelMapper.map(notificacion, NotificacionDTO.class);
    }

    @Override
    public NotificacionDTO createNotificacion(NotificacionDTO notificacionDTO) {
        Notificacion notificacion = modelMapper.map(notificacionDTO, Notificacion.class);
        if (notificacion.getId() != null && notificacionRepository.findById(notificacion.getId()).isPresent()){
            updateNotificacion(notificacion.getId(), notificacionDTO);
        }
        if (notificacionDTO.getIdUsuario() != null) {
            usuarioRepository.findById(notificacionDTO.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario not found with id: " + notificacionDTO.getIdUsuario()));
        }
        notificacion = notificacionRepository.save(notificacion);
        return modelMapper.map(notificacion, NotificacionDTO.class);
    }

    @Override
    public NotificacionDTO updateNotificacion(long id, NotificacionDTO notificacionDTO) {
        Notificacion existingNotificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificacion not found with id: " + id));
        existingNotificacion.setTitulo(notificacionDTO.getTitulo());
        existingNotificacion.setMensaje(notificacionDTO.getMensaje());
        existingNotificacion.setLeida(notificacionDTO.isLeida());
        if (notificacionDTO.getIdUsuario() != null) {
            Usuario usuario = usuarioRepository.findById(notificacionDTO.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario not found with id: " + notificacionDTO.getIdUsuario()));
            existingNotificacion.setUsuario(usuario);
        }
        existingNotificacion = notificacionRepository.save(existingNotificacion);
        return modelMapper.map(existingNotificacion, NotificacionDTO.class);
    }

    @Override
    public boolean deleteNotificacion(long id) {
        Notificacion existingNotificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificacion not found with id: " + id));
        notificacionRepository.delete(existingNotificacion);
        return true;
    }

    @Override
    public boolean desactivateNotificacion(long id) {
        Notificacion existingNotificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificacion not found with id: " + id));
        existingNotificacion.setEstado(!existingNotificacion.getEstado());
        notificacionRepository.save(existingNotificacion);
        return true;
    }
}
