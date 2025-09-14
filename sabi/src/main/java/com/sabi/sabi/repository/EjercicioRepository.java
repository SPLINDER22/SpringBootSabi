package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Ejercicio;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.enums.TipoEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    // Para evitar duplicados en el DataInitializer
    boolean existsByNombreAndTipo(String nombre, TipoEjercicio tipo);
    boolean existsByNombreAndTipoAndEntrenador(String nombre, TipoEjercicio tipo, Entrenador entrenador);

    // Para listar ejercicios activos (globales o del entrenador)
    @Query("SELECT e FROM Ejercicio e " +
            "WHERE e.estado = true AND " +
            "(e.tipo = com.sabi.sabi.entity.enums.TipoEjercicio.GLOBAL OR e.entrenador.id = :entrenadorId)")
    List<Ejercicio> findActivosByEntrenadorOrGlobal(@Param("entrenadorId") Long entrenadorId);
}
