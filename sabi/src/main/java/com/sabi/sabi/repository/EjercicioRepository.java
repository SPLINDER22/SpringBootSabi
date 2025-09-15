package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Ejercicio;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.enums.TipoEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    // Para evitar duplicados en el DataInitializer
    boolean existsByNombreAndTipo(String nombre, TipoEjercicio tipo);

    // Antes era con Entrenador, ahora con Usuario
    boolean existsByNombreAndTipoAndUsuario(String nombre, TipoEjercicio tipo, Usuario usuario);

    // Para listar ejercicios activos (globales o del usuario) con fetch del usuario
    @Query("SELECT e FROM Ejercicio e LEFT JOIN FETCH e.usuario u WHERE (u.id = :usuarioId OR u IS NULL) AND e.estado = true")
    List<Ejercicio> findActivosPorUsuario(@Param("usuarioId") Long usuarioId);

}
