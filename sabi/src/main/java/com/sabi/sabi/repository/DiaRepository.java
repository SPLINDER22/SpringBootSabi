package com.sabi.sabi.repository;

import com.sabi.sabi.dto.DiaDTO;
import com.sabi.sabi.entity.Dia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaRepository extends JpaRepository<Dia,Long> {
    List<Dia> findByEstadoTrue();

    @Query("SELECT d FROM Dia d WHERE d.semana.id = :idSemana AND d.estado = true ORDER BY d.numeroDia ASC")
    List<Dia> getDiasSemana(Long idSemana);
}
