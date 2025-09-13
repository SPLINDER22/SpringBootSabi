package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio,Long> {
    List<Ejercicio> findByEstadoTrue();
}
