package com.sabi.sabi.service;

import com.sabi.sabi.dto.UsuarioDTO;

import java.util.List;

public interface UsuarioService {
    List<UsuarioDTO> getAllUsuarios();
    List<UsuarioDTO> getAllActiveUsuarios();

    UsuarioDTO getUsuarioById(long id);

    UsuarioDTO createUsuario(UsuarioDTO usuarioDTO);

    UsuarioDTO updateUsuario(long id, UsuarioDTO usuarioDTO);

    boolean deleteUsuario(long id);

    boolean desactivateUsuario(long id);
}
