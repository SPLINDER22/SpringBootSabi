package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Entrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrenadorRepository extends JpaRepository<Entrenador,Long> {
    List<Entrenador> findByEstadoTrue();

    List<Entrenador> findByNombreContainingIgnoreCaseAndEstadoTrue(String nombre);
}
