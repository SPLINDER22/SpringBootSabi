package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Rutina;
import com.sabi.sabi.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina,Long> {
    List<Rutina> findByEstadoTrue();

    @Query("SELECT r FROM Rutina r LEFT JOIN FETCH r.entrenador e WHERE (e.id = :usuarioId) AND r.estado = true")
    List<Rutina> getRutinasPorUsuario(@Param("usuarioId") Long usuarioId);
}
