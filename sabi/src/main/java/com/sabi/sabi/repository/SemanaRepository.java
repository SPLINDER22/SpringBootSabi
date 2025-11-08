package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Rutina;
import com.sabi.sabi.entity.Semana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemanaRepository extends JpaRepository<Semana,Long> {
    List<Semana> findByEstadoTrue();

    @Query("SELECT s FROM Semana s WHERE s.rutina.id = :idRutina ORDER BY s.numeroSemana ASC")
    List<Semana> getSemanasRutina(Long idRutina);

    Semana findByRutina(Rutina rutina);
}
