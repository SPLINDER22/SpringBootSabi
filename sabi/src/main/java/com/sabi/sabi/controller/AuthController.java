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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        return "auth/registro";
    }

    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute UsuarioDTO usuarioDTO) {
        // Asignar rol temporal CLIENTE
        usuarioDTO.setRol(Rol.CLIENTE);

        // Guardar raw contraseña para autenticación programática
        String rawContraseña = usuarioDTO.getContraseña();

        // Encriptar la contraseña antes de guardar
        usuarioDTO.setContraseña(passwordEncoder.encode(rawContraseña));

        usuarioService.createUsuario(usuarioDTO);

        // Autenticar al usuario automáticamente para que pueda seleccionar rol
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(), rawContraseña);
        Authentication auth = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

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
        // Actualizar el contexto de seguridad con el nuevo rol
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(),
                usuario.getContraseña(),
                usuario.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        if (rol == Rol.CLIENTE) {
            return "redirect:/cliente/dashboard";
        } else if (rol == Rol.ENTRENADOR) {
            return "redirect:/entrenador/dashboard";
        } else {
            return "redirect:/";
        }
    }
}
