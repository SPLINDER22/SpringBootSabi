package com.sabi.sabi.repository;

import com.sabi.sabi.entity.EjercicioAsignado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioAsignadoRepository extends JpaRepository<EjercicioAsignado, Long> {
    List<EjercicioAsignado> findByEstadoTrue();

    @Query("SELECT e FROM EjercicioAsignado e JOIN FETCH e.ejercicio WHERE e.dia.id = :idDia AND e.estado = true ORDER BY e.orden ASC")
    List<EjercicioAsignado> getEjesDia(Long idDia);
}
