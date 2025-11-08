package com.sabi.sabi.repository;

import com.sabi.sabi.dto.RegistroSerieDTO;
import com.sabi.sabi.entity.RegistroSerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroSerieRepository extends JpaRepository<RegistroSerie,Long> {
    List<RegistroSerie> findByEstadoTrue();
    Optional<RegistroSerie> findFirstBySerie_Id(Long serieId);
    List<RegistroSerie> findBySerie_IdIn(List<Long> serieIds);
    List<RegistroSerie> findBySerie_Id(long idSerie);
}
