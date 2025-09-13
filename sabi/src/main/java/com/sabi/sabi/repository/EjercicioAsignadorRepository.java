package com.sabi.sabi.repository;

import com.sabi.sabi.entity.EjercicioAsignado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioAsignadorRepository extends JpaRepository<EjercicioAsignado,Long> {
    List<EjercicioAsignado> findByEstadoTrue();
}
