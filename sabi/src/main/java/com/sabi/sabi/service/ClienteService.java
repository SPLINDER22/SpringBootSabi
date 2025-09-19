package com.sabi.sabi.service;

import com.sabi.sabi.dto.ClienteDTO;
import com.sabi.sabi.dto.EntrenadorDTO;
import com.sabi.sabi.dto.DiagnosticoDTO;
import com.sabi.sabi.dto.RutinaDTO;

import java.util.List;

public interface ClienteService {
    List<ClienteDTO> getAllClientes();
    List<ClienteDTO> getAllActiveClientes();

    ClienteDTO getClienteById(long id);

    ClienteDTO createCliente(ClienteDTO clienteDTO);

    ClienteDTO updateCliente(long id, ClienteDTO clienteDTO);

    boolean deleteCliente(long id);

    List<EntrenadorDTO> getAllEntrenadores();

    DiagnosticoDTO getDiagnosticoActualByClienteId(Long clienteId);
    List<DiagnosticoDTO> getHistorialDiagnosticosByClienteId(Long clienteId);
    List<RutinaDTO> getRutinasByClienteId(Long clienteId);

    ClienteDTO getClienteByEmail(String email);
}
