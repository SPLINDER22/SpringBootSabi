package com.sabi.sabi.service;

import com.sabi.sabi.dto.ClienteDTO;

import java.util.List;

public interface ClienteService {
    List<ClienteDTO> getAllClientes();
    List<ClienteDTO> getAllActiveClientes();

    ClienteDTO getClienteById(long id);

    ClienteDTO createCliente(ClienteDTO clienteDTO);

    ClienteDTO updateCliente(long id, ClienteDTO clienteDTO);

    boolean deleteCliente(long id);
}
