package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByEstadoTrue();
    List<Comentario> findByEntrenadorIdAndEstadoTrue(Long entrenadorId);
    List<Comentario> findByClienteIdAndEstadoTrue(Long clienteId);
    List<Comentario> findByRutinaId(Long rutinaId);
    Optional<Comentario> findByClienteIdAndRutinaId(Long clienteId, Long rutinaId);
}
