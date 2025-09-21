package com.sabi.sabi.repository;

import com.sabi.sabi.entity.EjercicioAsignado;
import com.sabi.sabi.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SerieRepository extends JpaRepository<Serie,Long> {
    List<Serie> findByEstadoTrue();

    @Query("SELECT s FROM Serie s WHERE s.ejercicioAsignado.idEjercicioAsignado = " +
            ":idEjercicioAsignado AND s.estado = true ORDER BY s.orden ASC")
    List<Serie> getSerieEje(Long idEjercicioAsignado);

    List<Serie> findByEjercicioAsignado(EjercicioAsignado ejercicioAsignado);
}
