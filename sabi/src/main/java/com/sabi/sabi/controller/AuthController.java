package com.sabi.sabi.controller;

import com.sabi.sabi.dto.UsuarioDTO;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.enums.Rol;
import com.sabi.sabi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder; // <- agregado

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroForm() {
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute UsuarioDTO usuarioDTO) {
        // Asignar rol temporal CLIENTE
        usuarioDTO.setRol(Rol.CLIENTE);

        // Encriptar la contraseña antes de guardar
        usuarioDTO.setContraseña(passwordEncoder.encode(usuarioDTO.getContraseña()));

        usuarioService.createUsuario(usuarioDTO);

        return "redirect:/auth/seleccionar-rol";
    }

    @GetMapping("/seleccionar-rol")
    public String seleccionarRolForm() {
        return "auth/seleccionar-rol";
    }

    @PostMapping("/seleccionar-rol")
    public String seleccionarRol(
            @RequestParam Rol rol,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Usuario usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        usuario.setRol(rol);
        usuarioService.actualizarUsuario(usuario);
        return "redirect:/dashboard";
    }
}
