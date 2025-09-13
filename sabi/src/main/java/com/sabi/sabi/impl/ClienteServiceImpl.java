package com.sabi.sabi.impl;

import com.sabi.sabi.dto.ClienteDTO;
import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.repository.ClienteRepository;
import com.sabi.sabi.repository.UsuarioRepository;
import com.sabi.sabi.service.ClienteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ClienteDTO> getAllClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(cliente -> modelMapper.map(cliente, ClienteDTO.class))
                .toList();
    }

    @Override
    public List<ClienteDTO> getAllActiveClientes() {
        List<Cliente> clientes = clienteRepository.findByEstadoTrue();
        return clientes.stream()
                .map(cliente -> modelMapper.map(cliente, ClienteDTO.class))
                .toList();
    }

    @Override
    public ClienteDTO getClienteById(long id) {
        Cliente cliente = clienteRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cliente not found with id: " + id));
        return modelMapper.map(cliente, ClienteDTO.class);
    }

    @Override
    public ClienteDTO createCliente(ClienteDTO clienteDTO) {
        Cliente cliente = modelMapper.map(clienteDTO, Cliente.class);
        if (cliente.getId() != null && clienteRepository.findById(cliente.getId()).isPresent()){
            updateCliente(cliente.getId(), clienteDTO);
        }
        cliente = clienteRepository.save(cliente);
        return modelMapper.map(cliente, ClienteDTO.class);
    }

    @Override
    public ClienteDTO updateCliente(long id, ClienteDTO clienteDTO) {
        Cliente existingCliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente not found with id: " + id));

        // --- Actualizar atributos de Usuario ---
        existingCliente.setNombre(clienteDTO.getNombre());
        existingCliente.setEmail(clienteDTO.getEmail());
        existingCliente.setEstado(clienteDTO.getEstado());

        existingCliente.setObjetivos(clienteDTO.getObjetivos());
        existingCliente.setFechaNacimiento(clienteDTO.getFechaNacimiento());
        existingCliente.setSexo(clienteDTO.getSexo());
        existingCliente.setTelefono(clienteDTO.getTelefono());

        Cliente updatedCliente = clienteRepository.save(existingCliente);
        return modelMapper.map(updatedCliente, ClienteDTO.class);
    }

    @Override
    public boolean deleteCliente(long id) {
        if (!clienteRepository.findById(id).isPresent()) {
            throw new RuntimeException("Cliente not found with id: " + id);
        }
        clienteRepository.deleteById(id);
        return true;
    }
}
