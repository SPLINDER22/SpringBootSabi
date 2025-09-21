package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Suscripcion;
import com.sabi.sabi.entity.enums.EstadoSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion,Long> {
    List<Suscripcion> findByEstadoTrue();

    Optional<Suscripcion> findTopByCliente_IdAndEstadoTrueAndEstadoSuscripcionIsNotOrderByIdDesc(Long clienteId, EstadoSuscripcion notEstado);

    boolean existsByCliente_IdAndEstadoTrueAndEstadoSuscripcionIsNot(Long clienteId, EstadoSuscripcion notEstado);

    List<Suscripcion> findByCliente_IdAndEstadoTrueAndEstadoSuscripcion(Long clienteId, EstadoSuscripcion estado);
}
