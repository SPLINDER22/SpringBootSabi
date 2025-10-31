package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Entrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrenadorRepository extends JpaRepository<Entrenador,Long> {
    List<Entrenador> findByEstadoTrue();

    List<Entrenador> findByNombreContainingIgnoreCaseAndEstadoTrue(String nombre);

    @Query("SELECT e FROM Entrenador e " +
            "WHERE e.estado = true " +
            "AND (:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND (:especialidad IS NULL OR LOWER(e.especialidad) LIKE LOWER(CONCAT('%', :especialidad, '%'))) " +
            "AND (:min IS NULL OR e.calificacionPromedio >= :min) " +
            "AND (:max IS NULL OR e.calificacionPromedio <= :max)")
    List<Entrenador> searchActive(
            @Param("nombre") String nombre,
            @Param("especialidad") String especialidad,
            @Param("min") Double min,
            @Param("max") Double max
    );
}
