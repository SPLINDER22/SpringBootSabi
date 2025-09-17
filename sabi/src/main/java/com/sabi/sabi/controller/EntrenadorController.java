package com.sabi.sabi.controller;

import com.sabi.sabi.service.EntrenadorService;
import com.sabi.sabi.service.SuscripcionService;
import com.sabi.sabi.service.UsuarioService;
import com.sabi.sabi.dto.SuscripcionDTO;
import com.sabi.sabi.entity.enums.EstadoSuscripcion;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EntrenadorController {
    @Autowired
    private EntrenadorService entrenadorService;

    @Autowired
    private SuscripcionService suscripcionService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/entrenador/dashboard")
    public String entrenadorDashboard() {
        return "entrenador/dashboard";
    }

    @GetMapping("/entrenadores")
    public String getAllActiveEntrenadores(Model model) {
        model.addAttribute("entrenadores", entrenadorService.getAllActiveEntrenadores());
        return "cliente/listaEntrenadores";
    }

    @org.springframework.web.bind.annotation.PostMapping("/entrenadores/solicitar/{idEntrenador}")
    public String solicitarEntrenador(@org.springframework.web.bind.annotation.PathVariable Long idEntrenador,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        // Crear la suscripción asociando cliente y entrenador
        SuscripcionDTO suscripcionDTO = new SuscripcionDTO();
        suscripcionDTO.setIdCliente(usuario.getId());
        suscripcionDTO.setIdEntrenador(idEntrenador);
        suscripcionDTO.setEstadoSuscripcion(EstadoSuscripcion.PENDIENTE);
        suscripcionService.createSuscripcion(suscripcionDTO);
        return "redirect:/suscripciones";
    }
}
