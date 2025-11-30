package com.sabi.sabi.controller;

import com.sabi.sabi.entity.Entrenador;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.enums.Rol;
import com.sabi.sabi.repository.UsuarioRepository;
import com.sabi.sabi.repository.EntrenadorRepository;
import com.sabi.sabi.service.EmailService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final EmailService emailService;

    public AdminController(UsuarioRepository usuarioRepository, EntrenadorRepository entrenadorRepository, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.emailService = emailService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalUsuarios = usuarioRepository.count();
        long totalEntrenadores = entrenadorRepository.count();
        long totalClientes = usuarioRepository.findAll().stream().filter(u -> u.getRol() == Rol.CLIENTE).count();
        long verificados = entrenadorRepository.findAll().stream().filter(Entrenador::isVerified).count();
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalEntrenadores", totalEntrenadores);
        model.addAttribute("entrenadoresVerificados", verificados);
        return "admin/dashboard";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios";
    }

    @GetMapping("/entrenadores")
    public String entrenadores(Model model) {
        List<Entrenador> entrenadores = entrenadorRepository.findAll();
        model.addAttribute("entrenadores", entrenadores);
        return "admin/entrenadores";
    }

    @PostMapping("/usuarios/{id}/bloquear")
    public String bloquear(@PathVariable Long id, @RequestParam(value = "motivo", required = false) String motivo) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            // Enviar correo previo al bloqueo
            try { emailService.enviarAvisoBloqueo(usuario.getEmail(), motivo); } catch (Exception ignored) {}
            usuario.setEstado(false);
            usuarioRepository.save(usuario);
        });
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/desbloquear")
    public String desbloquear(@PathVariable Long id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setEstado(true);
            usuarioRepository.save(usuario);
        });
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/entrenadores/{id}/verificar")
    public String verificar(@PathVariable Long id) {
        entrenadorRepository.findById(id).ifPresent(entrenador -> {
            entrenador.setVerified(true);
            entrenadorRepository.save(entrenador);
            try { emailService.enviarAvisoVerificacion(entrenador.getEmail()); } catch (Exception ignored) {}
        });
        return "redirect:/admin/entrenadores";
    }

    @PostMapping("/entrenadores/{id}/revocar")
    public String revocar(@PathVariable Long id) {
        entrenadorRepository.findById(id).ifPresent(entrenador -> {
            entrenador.setVerified(false);
            entrenadorRepository.save(entrenador);
        });
        return "redirect:/admin/entrenadores";
    }
}

