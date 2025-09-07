package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Semana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemanaRepository extends JpaRepository<Semana,Long> {
}
