package com.sabi.sabi.service;

import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Rutina;

import java.util.List;

public interface RutinaService {
    List<RutinaDTO> getAllRutinas();
    List<RutinaDTO> getAllActiveRutinas();

    List<RutinaDTO> getRutinasPorUsuario(Long usuarioId);
    List<Rutina> findByClienteAndEstado(Cliente cliente);

    RutinaDTO getRutinaById(long id);

    RutinaDTO createRutina(RutinaDTO rutinaDTO);

    RutinaDTO updateRutina(long id, RutinaDTO rutinaDTO);

    boolean deleteRutina(long id);

    boolean desactivateRutina(long id);

    @Deprecated
    void finalizarRutinaCliente(long idRutina);
    void finalizarRutinaCliente(long idRutina, long idCliente);

    RutinaDTO getRutinaActivaCliente(Long clienteId);
    List<RutinaDTO> getRutinasGlobales();

    void adoptarRutina(long idRutina, long idCliente);
}
