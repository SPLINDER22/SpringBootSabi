package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    List<Usuario> findByEstadoTrue();
}
