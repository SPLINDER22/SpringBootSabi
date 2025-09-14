package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio,Long> {
    @Query("SELECT e FROM Ejercicio e " +
            "WHERE e.estado = true AND (e.tipo = 'GLOBAL' OR e.entrenador.id = :entrenadorId)")
    List<Ejercicio> findActivosByEntrenadorOrGlobal(@Param("entrenadorId") Long entrenadorId);
}
