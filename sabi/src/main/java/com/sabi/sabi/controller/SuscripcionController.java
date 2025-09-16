package com.sabi.sabi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sabi.sabi.service.ClienteService;
import com.sabi.sabi.service.EntrenadorService;
import com.sabi.sabi.service.SuscripcionService;
import com.sabi.sabi.service.UsuarioService;

@Controller
public class SuscripcionController {

    @Autowired
    private SuscripcionService suscripcionService;
    @Autowired
    private EntrenadorService entrenadorService;
    @Autowired
    private ClienteService clienteService;


    @org.springframework.beans.factory.annotation.Autowired
    private UsuarioService usuarioService;

    @org.springframework.web.bind.annotation.GetMapping("/suscripciones")
    public String listarSuscripciones(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails, org.springframework.ui.Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        model.addAttribute("suscripciones", suscripcionService.getAllSuscripciones());
        model.addAttribute("idUsuarioActual", usuario.getId());
        return "suscripciones/lista";
    }

    @org.springframework.web.bind.annotation.GetMapping("/suscripciones/nuevo")
    public String crearSuscripcionView(org.springframework.ui.Model model) {
        com.sabi.sabi.dto.SuscripcionDTO suscripcionDTO = new com.sabi.sabi.dto.SuscripcionDTO();
        model.addAttribute("suscripcion", suscripcionDTO);
        model.addAttribute("clientes", clienteService.getAllActiveClientes());
        model.addAttribute("entrenadores", entrenadorService.getAllActiveEntrenadores());
        return "suscripciones/formulario";
    }

    @org.springframework.web.bind.annotation.GetMapping("/suscripciones/editar/{id}")
    public String editarSuscripcionView(@org.springframework.web.bind.annotation.PathVariable Long id,
                                        @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
                                        org.springframework.ui.Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        var suscripcionDTO = suscripcionService.getSuscripcionById(id);
        if (suscripcionDTO == null) {
            return "redirect:/suscripciones?error=notfound";
        }
        model.addAttribute("suscripcion", suscripcionDTO);
        model.addAttribute("clientes", clienteService.getAllActiveClientes());
        model.addAttribute("entrenadores", entrenadorService.getAllActiveEntrenadores());
        return "suscripciones/formulario";
    }

    @org.springframework.web.bind.annotation.PostMapping("/suscripciones/guardar")
    public String crearSuscripcion(@org.springframework.web.bind.annotation.ModelAttribute com.sabi.sabi.dto.SuscripcionDTO suscripcionDTO,
                                   @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        // Si viene ID (update)
        if (suscripcionDTO.getIdSuscripcion() != null) {
            suscripcionService.updateSuscripcion(suscripcionDTO.getIdSuscripcion(), suscripcionDTO);
        } else {
            suscripcionService.createSuscripcion(suscripcionDTO);
        }
        return "redirect:/suscripciones";
    }

    @org.springframework.web.bind.annotation.PostMapping("/suscripciones/eliminar/{id}")
    public String eliminarSuscripcion(@org.springframework.web.bind.annotation.PathVariable Long id,
                                      @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        suscripcionService.deleteSuscripcion(id);
        return "redirect:/suscripciones";
    }

    @org.springframework.web.bind.annotation.PostMapping("/suscripciones/desactivar/{id}")
    public String desactivarSuscripcion(@org.springframework.web.bind.annotation.PathVariable Long id,
                                        @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        suscripcionService.desactivateSuscripcion(id);
        return "redirect:/suscripciones";
    }
}
