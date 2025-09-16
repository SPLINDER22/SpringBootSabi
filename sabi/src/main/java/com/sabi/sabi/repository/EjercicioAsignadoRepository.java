package com.sabi.sabi.repository;

import com.sabi.sabi.entity.EjercicioAsignado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EjercicioAsignadoRepository extends JpaRepository<EjercicioAsignado, Long> {
}

