package com.sabi.sabi.impl;

import com.sabi.sabi.dto.UsuarioDTO;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.repository.UsuarioRepository;
import com.sabi.sabi.service.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ModelMapper modelMapper;

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
        Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);
        if (usuario.getId() != null && usuarioRepository.findById(usuario.getId()).isPresent()){
            updateUsuario(usuario.getId(), usuarioDTO);
        }
        usuario = usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    @Override
    public UsuarioDTO updateUsuario(long id, UsuarioDTO usuarioDTO) {
        Usuario existingUsuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario not found with id: " + id));
        existingUsuario.setNombre(usuarioDTO.getNombre());
        existingUsuario.setEmail(usuarioDTO.getEmail());
        existingUsuario.setEstado(usuarioDTO.getEstado());
        existingUsuario = usuarioRepository.save(existingUsuario);
        return modelMapper.map(existingUsuario, UsuarioDTO.class);
    }
    @Override
    public void actualizarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }


    @Override
    public boolean deleteUsuario(long id) {
        if (!usuarioRepository.findById(id).isPresent()){
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
