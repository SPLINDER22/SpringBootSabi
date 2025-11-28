package com.sabi.sabi.repository;

import com.sabi.sabi.entity.MensajePregrabado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajePregrabadoRepository extends JpaRepository<MensajePregrabado, Long> {
    List<MensajePregrabado> findByEntrenadorIdAndActivoTrue(Long entrenadorId);
    List<MensajePregrabado> findByEntrenadorIdOrderByFechaCreacionDesc(Long entrenadorId);
}
