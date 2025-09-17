package com.sabi.sabi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.sabi.sabi.dto.SuscripcionDTO;
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


    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/suscripciones")
    public String listarSuscripciones(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        model.addAttribute("suscripciones", suscripcionService.getAllSuscripciones());
        model.addAttribute("idUsuarioActual", usuario.getId());
        return "suscripciones/lista";
    }

    @GetMapping("/suscripciones/nuevo")
    public String crearSuscripcionView(Model model) {
        SuscripcionDTO suscripcionDTO = new SuscripcionDTO();
        model.addAttribute("suscripcion", suscripcionDTO);
        model.addAttribute("clientes", clienteService.getAllActiveClientes());
        model.addAttribute("entrenadores", entrenadorService.getAllActiveEntrenadores());
        return "suscripciones/formulario";
    }

    @GetMapping("/suscripciones/editar/{id}")
    public String editarSuscripcionView(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        Model model) {
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

    @PostMapping("/suscripciones/guardar")
    public String crearSuscripcion(@ModelAttribute SuscripcionDTO suscripcionDTO,
                                   @AuthenticationPrincipal UserDetails userDetails) {
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


    @PostMapping("/suscripciones/pagar/{id}")
    public String pagarSuscripcion(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var suscripcion = suscripcionService.getSuscripcionById(id);
        if (suscripcion != null) {
            suscripcion.setEstadoSuscripcion(com.sabi.sabi.entity.enums.EstadoSuscripcion.ACEPTADA);
            suscripcionService.updateSuscripcion(id, suscripcion);
        }
        return "redirect:/suscripciones";
    }

    @PostMapping("/suscripciones/rechazar/{id}")
    public String rechazarSuscripcion(@PathVariable Long id,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var suscripcion = suscripcionService.getSuscripcionById(id);
        if (suscripcion != null) {
            suscripcion.setEstadoSuscripcion(com.sabi.sabi.entity.enums.EstadoSuscripcion.RECHAZADA);
            suscripcionService.updateSuscripcion(id, suscripcion);
        }
        return "redirect:/suscripciones";
    }

    @PostMapping("/suscripciones/eliminar/{id}")
    public String eliminarSuscripcion(@PathVariable Long id,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        suscripcionService.deleteSuscripcion(id);
        return "redirect:/suscripciones";
    }

    @PostMapping("/suscripciones/desactivar/{id}")
    public String desactivarSuscripcion(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        suscripcionService.desactivateSuscripcion(id);
        return "redirect:/suscripciones";
    }
}
