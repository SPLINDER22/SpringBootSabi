package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SerieRepository extends JpaRepository<Serie,Long> {
    List<Serie> findByEstadoTrue();
}
