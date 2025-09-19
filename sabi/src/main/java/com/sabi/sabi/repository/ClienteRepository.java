package com.sabi.sabi.repository;

import com.sabi.sabi.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByEstadoTrue();
    Cliente findByEmail(String email);
}
