package com.sabi.sabi.service;

import com.sabi.sabi.dto.NotificacionDTO;

import java.util.List;

public interface NotificacionService {
    List<NotificacionDTO> getAllNotificaciones();
    List<NotificacionDTO> getAllActiveNotificaciones();

    NotificacionDTO getNotificacionById(long id);

    NotificacionDTO createNotificacion(NotificacionDTO notificacionDTO);

    NotificacionDTO updateNotificacion(long id, NotificacionDTO notificacionDTO);

    boolean deleteNotificacion(long id);

    boolean desactivateNotificacion(long id);
}
