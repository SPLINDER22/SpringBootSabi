package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Combo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComboRepository extends JpaRepository<Combo,Long> {

    // Buscar combos por el ID del ejercicio (compartido con EjercicioAsignado)
    List<Combo> findByEjercicioId(Long ejercicioId);

    // Alternativa con query personalizada si necesitas buscar por idEjercicioAsignado directamente
    @Query("SELECT c FROM Combo c WHERE c.ejercicio.id = (SELECT ea.ejercicio.id FROM EjercicioAsignado ea WHERE ea.idEjercicioAsignado = :idEjercicioAsignado)")
    List<Combo> findByEjercicioAsignadoId(@Param("idEjercicioAsignado") Long idEjercicioAsignado);
}
