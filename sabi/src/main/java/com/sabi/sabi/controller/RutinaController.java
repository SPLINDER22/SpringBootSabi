package com.sabi.sabi.controller;

import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.service.RutinaService;
import com.sabi.sabi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RutinaController {
    @Autowired
    private RutinaService rutinaService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/rutinas")
    public String listarRutinas(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario;
        try {
            usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        } catch (RuntimeException ex) {
            return "redirect:/auth/login";
        }
        // Validar que el usuario sea entrenador
        if (usuario.getRol() == null || !usuario.getRol().name().equalsIgnoreCase("ENTRENADOR")) {
            return "redirect:/cliente/dashboard"; // O redirigir al dashboard del cliente si lo prefieres
        }
        model.addAttribute("rutinas", rutinaService.getRutinasPorUsuario(usuario.getId()));
        model.addAttribute("idUsuarioActual", usuario.getId());
        return "rutinas/lista";
    }
}
