package com.sabi.sabi.impl;

import com.sabi.sabi.dto.UsuarioDTO;
import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.enums.Rol;
import com.sabi.sabi.repository.UsuarioRepository;
import com.sabi.sabi.service.UsuarioService;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @PostConstruct
    public void init() {
        logger.debug("UsuarioServiceImpl se ha inicializado correctamente.");
    }

    @Override
    public List<UsuarioDTO> getAllUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .toList();
    }

    @Override
    public List<UsuarioDTO> getAllActiveUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findByEstadoTrue();
        return usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .toList();
    }

    @Override
    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }


    @Override
    public UsuarioDTO getUsuarioById(long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Usuario not found with id: " + id));
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    @Override
    public UsuarioDTO createUsuario(UsuarioDTO usuarioDTO) {
        // Si viene ID, actualizar
        if (usuarioDTO.getId() != null && usuarioRepository.findById(usuarioDTO.getId()).isPresent()){
            return updateUsuario(usuarioDTO.getId(), usuarioDTO);
        }

        // Crear subclase según rol para que exista la fila en la tabla específica
        Rol rol = usuarioDTO.getRol();
        Usuario toSave;
        if (rol == Rol.CLIENTE) {
            toSave = Cliente.builder()
                    .nombre(usuarioDTO.getNombre())
                    .apellido(usuarioDTO.getApellido())
                    .email(usuarioDTO.getEmail())
                    .contraseña(usuarioDTO.getContraseña())
                    .rol(Rol.CLIENTE)
                    .sexo(usuarioDTO.getSexo())
                    .fechaNacimiento(usuarioDTO.getFechaNacimiento())
                    .departamento(usuarioDTO.getDepartamento())
                    .ciudad(usuarioDTO.getCiudad())
                    .tipoDocumento(usuarioDTO.getTipoDocumento())
                    .numeroDocumento(usuarioDTO.getNumeroDocumento())
                    .telefono(usuarioDTO.getTelefono())
                    .estado(usuarioDTO.getEstado() != null ? usuarioDTO.getEstado() : true)
                    .build();
        } else if (rol == Rol.ENTRENADOR) {
            toSave = Entrenador.builder()
                    .nombre(usuarioDTO.getNombre())
                    .apellido(usuarioDTO.getApellido())
                    .email(usuarioDTO.getEmail())
                    .contraseña(usuarioDTO.getContraseña())
                    .rol(Rol.ENTRENADOR)
                    .sexo(usuarioDTO.getSexo())
                    .fechaNacimiento(usuarioDTO.getFechaNacimiento())
                    .departamento(usuarioDTO.getDepartamento())
                    .ciudad(usuarioDTO.getCiudad())
                    .tipoDocumento(usuarioDTO.getTipoDocumento())
                    .numeroDocumento(usuarioDTO.getNumeroDocumento())
                    .telefono(usuarioDTO.getTelefono())
                    .estado(usuarioDTO.getEstado() != null ? usuarioDTO.getEstado() : true)
                    .calificacionPromedio(0.0)
                    .aniosExperiencia(0)
                    .build();
        } else {
            // Por defecto, crea usuario base
            toSave = Usuario.builder()
                    .nombre(usuarioDTO.getNombre())
                    .apellido(usuarioDTO.getApellido())
                    .email(usuarioDTO.getEmail())
                    .contraseña(usuarioDTO.getContraseña())
                    .rol(rol != null ? rol : Rol.CLIENTE)
                    .sexo(usuarioDTO.getSexo())
                    .fechaNacimiento(usuarioDTO.getFechaNacimiento())
                    .departamento(usuarioDTO.getDepartamento())
                    .ciudad(usuarioDTO.getCiudad())
                    .tipoDocumento(usuarioDTO.getTipoDocumento())
                    .numeroDocumento(usuarioDTO.getNumeroDocumento())
                    .telefono(usuarioDTO.getTelefono())
                    .estado(usuarioDTO.getEstado() != null ? usuarioDTO.getEstado() : true)
                    .build();
        }

        toSave = usuarioRepository.save(toSave);
        return modelMapper.map(toSave, UsuarioDTO.class);
    }

    @Override
    public UsuarioDTO updateUsuario(long id, UsuarioDTO usuarioDTO) {
        Usuario existingUsuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario not found with id: " + id));
        existingUsuario.setNombre(usuarioDTO.getNombre());
        existingUsuario.setEmail(usuarioDTO.getEmail());
        if (usuarioDTO.getContraseña() != null && !usuarioDTO.getContraseña().isBlank()) {
            existingUsuario.setContraseña(usuarioDTO.getContraseña()); // Cambiado desde 'getPassword' para consistencia con el idioma español
        }
        if (usuarioDTO.getRol() != null) {
            existingUsuario.setRol(usuarioDTO.getRol());
        }
        if (usuarioDTO.getEstado() != null) {
            existingUsuario.setEstado(usuarioDTO.getEstado());
        }
        existingUsuario = usuarioRepository.save(existingUsuario);
        return modelMapper.map(existingUsuario, UsuarioDTO.class);
    }
    @Override
    public void actualizarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }


    @Override
    public boolean deleteUsuario(long id) {
        if (usuarioRepository.findById(id).isEmpty()){
            throw new RuntimeException("Usuario not found with id: " + id);
        }
        usuarioRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean desactivateUsuario(long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario not found with id: " + id));
        usuario.setEstado(!usuario.getEstado());
        usuarioRepository.save(usuario);
        return true;
    }
}
