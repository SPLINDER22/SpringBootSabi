package com.sabi.sabi.repository;

import com.sabi.sabi.entity.RegistroSerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroSerieRepository extends JpaRepository<RegistroSerie,Long> {
}
