package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Semana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemanaRepository extends JpaRepository<Semana,Long> {
    List<Semana> findByEstadoTrue();
}
