package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Dia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaRepository extends JpaRepository<Dia,Long> {
    List<Dia> findByEstadoTrue();
}
