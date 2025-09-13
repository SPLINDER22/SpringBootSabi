package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion,Long> {
    List<Notificacion> findByEstadoTrue();
}
