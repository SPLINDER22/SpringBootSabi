package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion,Long> {
    List<Suscripcion> findByEstadoTrue();
}
