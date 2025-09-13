package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina,Long> {
    List<Rutina> findByEstadoTrue();
}
