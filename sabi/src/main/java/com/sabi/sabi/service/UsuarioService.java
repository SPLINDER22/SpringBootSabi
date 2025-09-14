package com.sabi.sabi.service;

import com.sabi.sabi.dto.UsuarioDTO;
import com.sabi.sabi.entity.Usuario;

import java.util.List;

public interface UsuarioService {
    List<UsuarioDTO> getAllUsuarios();
    List<UsuarioDTO> getAllActiveUsuarios();

    Usuario obtenerPorEmail(String email);

    UsuarioDTO getUsuarioById(long id);

    UsuarioDTO createUsuario(UsuarioDTO usuarioDTO);

    UsuarioDTO updateUsuario(long id, UsuarioDTO usuarioDTO);
    void actualizarUsuario(Usuario usuario);

    boolean deleteUsuario(long id);

    boolean desactivateUsuario(long id);
}
