package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina,Long> {
    List<Rutina> findByEstadoTrue();
    List<Rutina> findByClienteIdAndEstadoTrue(Long clienteId);

    @Query("SELECT r FROM Rutina r LEFT JOIN FETCH r.entrenador e WHERE (e.id = :usuarioId) AND r.estado = true AND r.cliente IS NULL")
    List<Rutina> getRutinasPorUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT r FROM Rutina r WHERE r.cliente = :cliente AND r.estadoRutina = 'ACTIVA' AND r.estado = true")
    List<Rutina> findByClienteAndEstado(@Param("cliente") Cliente cliente);

    @Query("SELECT r FROM Rutina r WHERE r.cliente.id = :clienteId AND r.estadoRutina = 'ACTIVA' AND r.estado = true")
    Optional<Rutina> findActiveByClienteId(@Param("clienteId") Long clienteId);

    // Rutinas globales activas sin cliente ni entrenador
    List<Rutina> findByClienteIsNullAndEntrenadorIsNullAndEstadoTrue();

    // (Deprecated) preexistente: se mantiene si otro código lo usa todavía
    List<Rutina> findByClienteIsNullAndEstadoTrue();
}
