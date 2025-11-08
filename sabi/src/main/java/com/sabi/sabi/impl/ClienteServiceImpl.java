package com.sabi.sabi.impl;

import com.sabi.sabi.dto.ClienteDTO;
import com.sabi.sabi.dto.DiagnosticoDTO;
import com.sabi.sabi.dto.EntrenadorDTO;
import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Diagnostico;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Rutina;
import com.sabi.sabi.repository.ClienteRepository;
import com.sabi.sabi.repository.DiagnosticoRepository;
import com.sabi.sabi.repository.EntrenadorRepository;
import com.sabi.sabi.repository.RutinaRepository;
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
    private EntrenadorRepository entrenadorRepository;
    @Autowired
    private DiagnosticoRepository diagnosticoRepository;
    @Autowired
    private RutinaRepository rutinaRepository;
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

        // --- Actualizar atributos de Usuario (heredados) ---
        if (clienteDTO.getNombre() != null) {
            existingCliente.setNombre(clienteDTO.getNombre());
        }
        if (clienteDTO.getApellido() != null) {
            existingCliente.setApellido(clienteDTO.getApellido());
        }
        if (clienteDTO.getEmail() != null) {
            existingCliente.setEmail(clienteDTO.getEmail());
        }
        if (clienteDTO.getSexo() != null) {
            existingCliente.setSexo(clienteDTO.getSexo());
        }
        if (clienteDTO.getFechaNacimiento() != null) {
            existingCliente.setFechaNacimiento(clienteDTO.getFechaNacimiento());
        }
        if (clienteDTO.getDepartamento() != null) {
            existingCliente.setDepartamento(clienteDTO.getDepartamento());
        }
        if (clienteDTO.getCiudad() != null) {
            existingCliente.setCiudad(clienteDTO.getCiudad());
        }
        if (clienteDTO.getTipoDocumento() != null) {
            existingCliente.setTipoDocumento(clienteDTO.getTipoDocumento());
        }
        if (clienteDTO.getNumeroDocumento() != null) {
            existingCliente.setNumeroDocumento(clienteDTO.getNumeroDocumento());
        }
        if (clienteDTO.getTelefono() != null) {
            existingCliente.setTelefono(clienteDTO.getTelefono());
        }
        if (clienteDTO.getFotoPerfilUrl() != null) {
            existingCliente.setFotoPerfilUrl(clienteDTO.getFotoPerfilUrl());
        }
        if (clienteDTO.getDescripcion() != null) {
            existingCliente.setDescripcion(clienteDTO.getDescripcion());
        }
        if (clienteDTO.getEstado() != null) {
            existingCliente.setEstado(clienteDTO.getEstado());
        }

        // --- Actualizar atributos específicos de Cliente ---
        if (clienteDTO.getObjetivo() != null) {
            existingCliente.setObjetivo(clienteDTO.getObjetivo());
        }

        Cliente updatedCliente = clienteRepository.save(existingCliente);
        return modelMapper.map(updatedCliente, ClienteDTO.class);
    }

    @Override
    public boolean deleteCliente(long id) {
        if (clienteRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Cliente not found with id: " + id);
        }
        clienteRepository.deleteById(id);
        return true;
    }

    @Override
    public List<EntrenadorDTO> getAllEntrenadores() {
        List<Entrenador> entrenadores = entrenadorRepository.findByEstadoTrue();
        return entrenadores.stream()
                .map(entrenador -> modelMapper.map(entrenador, EntrenadorDTO.class))
                .toList();
    }

    @Override
    public DiagnosticoDTO getDiagnosticoActualByClienteId(Long clienteId) {
        List<Diagnostico> diagnosticos = diagnosticoRepository.findByClienteIdAndEstadoTrue(clienteId);
        if (diagnosticos.isEmpty()) return null;
        Diagnostico diagnosticoActual = diagnosticos.stream()
            .max((d1, d2) -> d1.getFecha().compareTo(d2.getFecha()))
            .orElse(diagnosticos.get(0));
        return modelMapper.map(diagnosticoActual, DiagnosticoDTO.class);
    }

    @Override
    public List<DiagnosticoDTO> getHistorialDiagnosticosByClienteId(Long clienteId) {
        System.out.println("\n========================================");
        System.out.println("🔍 LLAMADA A getHistorialDiagnosticosByClienteId");
        System.out.println("   Cliente ID: " + clienteId);

        // Obtener diagnósticos ordenados por fecha descendente (más reciente primero)
        List<Diagnostico> diagnosticos = diagnosticoRepository.findByClienteIdAndEstadoTrueOrderByFechaDesc(clienteId);

        System.out.println("   Diagnósticos encontrados en BD: " + (diagnosticos != null ? diagnosticos.size() : "null"));

        if (diagnosticos != null && !diagnosticos.isEmpty()) {
            System.out.println("   📋 LISTADO DE DIAGNÓSTICOS:");
            for (int i = 0; i < diagnosticos.size(); i++) {
                Diagnostico d = diagnosticos.get(i);
                System.out.println("      [" + i + "] ID: " + d.getId() +
                                 ", Fecha: " + d.getFecha() +
                                 ", Peso: " + d.getPeso() + " kg" +
                                 ", Estado: " + d.getEstado() +
                                 ", Cliente ID: " + (d.getCliente() != null ? d.getCliente().getId() : "null"));
            }
        } else {
            System.out.println("   ⚠️ NO SE ENCONTRARON DIAGNÓSTICOS");
            System.out.println("   Verificando si existen diagnósticos SIN FILTRO de estado...");
            List<Diagnostico> todosDiagnosticos = diagnosticoRepository.findAll();
            System.out.println("   Total diagnósticos en BD (sin filtro): " + todosDiagnosticos.size());
            if (!todosDiagnosticos.isEmpty()) {
                System.out.println("   Mostrando TODOS los diagnósticos:");
                for (Diagnostico d : todosDiagnosticos) {
                    System.out.println("      - ID: " + d.getId() +
                                     ", Cliente ID: " + (d.getCliente() != null ? d.getCliente().getId() : "null") +
                                     ", Estado: " + d.getEstado() +
                                     ", Fecha: " + d.getFecha());
                }
            }
        }

        List<DiagnosticoDTO> resultado = diagnosticos.stream()
            .map(d -> modelMapper.map(d, DiagnosticoDTO.class))
            .toList();

        System.out.println("   📤 DTOs mapeados: " + resultado.size());
        System.out.println("========================================\n");

        return resultado;
    }

    @Override
    public List<RutinaDTO> getRutinasByClienteId(Long clienteId) {
        List<Rutina> rutinas = rutinaRepository.findByClienteIdAndEstadoTrue(clienteId);
        return rutinas.stream()
            .map(r -> modelMapper.map(r, RutinaDTO.class))
            .toList();
    }

    @Override
    public ClienteDTO getClienteByEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) return null;
        return modelMapper.map(cliente, ClienteDTO.class);
    }
}
