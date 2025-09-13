package com.sabi.sabi.repository;

import com.sabi.sabi.entity.RegistroSerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroSerieRepository extends JpaRepository<RegistroSerie,Long> {
    List<RegistroSerie> findByEstadoTrue();
}
