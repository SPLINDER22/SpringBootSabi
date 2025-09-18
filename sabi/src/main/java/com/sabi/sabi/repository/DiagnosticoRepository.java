package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Diagnostico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosticoRepository extends JpaRepository<Diagnostico,Long> {
    List<Diagnostico> findByEstadoTrue();
    List<Diagnostico> findByClienteIdAndEstadoTrue(Long clienteId);
}
