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

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                return "redirect:/cliente/dashboard";
            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ENTRENADOR"))) {
                return "redirect:/entrenador/dashboard";
            }
            return "redirect:/";
        }
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        return "auth/registro";
    }

    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute UsuarioDTO usuarioDTO,
                                   @RequestParam(required = false) String genero,
                                   @RequestParam(required = false) String fechaNacimiento,
                                   @RequestParam(required = false) String departamento,
                                   @RequestParam(required = false) String ciudad,
                                   @RequestParam(required = false) String tipoDocumento,
                                   @RequestParam(required = false) String numeroDocumento,
                                   @RequestParam(required = false) String telefono,
                                   Model model) {

        try {
            // Convertir los parámetros String a los tipos enum correspondientes
            if (genero != null && !genero.isEmpty()) {
                usuarioDTO.setSexo(com.sabi.sabi.entity.enums.Sexo.valueOf(genero));
            }

            if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
                usuarioDTO.setFechaNacimiento(java.time.LocalDate.parse(fechaNacimiento));
            }

            if (departamento != null && !departamento.isEmpty()) {
                usuarioDTO.setDepartamento(com.sabi.sabi.entity.enums.Departamento.valueOf(departamento));
            }

            if (ciudad != null && !ciudad.isEmpty()) {
                usuarioDTO.setCiudad(ciudad);
            }

            if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
                usuarioDTO.setTipoDocumento(com.sabi.sabi.entity.enums.TipoDocumento.valueOf(tipoDocumento));
            }

            if (numeroDocumento != null && !numeroDocumento.isEmpty()) {
                usuarioDTO.setNumeroDocumento(numeroDocumento);
            }

            if (telefono != null && !telefono.isEmpty()) {
                usuarioDTO.setTelefono(telefono);
            }

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
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "auth/registro";
        }
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

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Invalidar la sesión
        request.getSession().invalidate();
        // Limpiar el contexto de seguridad
        SecurityContextHolder.clearContext();
        // Redirigir al login con mensaje
        return "redirect:/auth/login?logoutMessage=Has cerrado sesión exitosamente.";
    }
}
