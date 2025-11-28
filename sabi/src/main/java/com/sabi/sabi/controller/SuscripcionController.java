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
import org.springframework.web.bind.annotation.RequestParam;

import com.sabi.sabi.dto.SuscripcionDTO;
import com.sabi.sabi.service.ClienteService;
import com.sabi.sabi.service.EntrenadorService;
import com.sabi.sabi.service.SuscripcionService;
import com.sabi.sabi.service.UsuarioService;
import com.sabi.sabi.service.RutinaService;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    private RutinaService rutinaService; // nuevo

    @GetMapping("/suscripciones")
    public String listarSuscripciones(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        var suscripciones = suscripcionService.getAllSuscripciones();
        if (suscripciones == null) {
            suscripciones = java.util.Collections.emptyList();
        }
        model.addAttribute("suscripciones", suscripciones);
        // Construir mapas de nombres con defensas contra null
        java.util.Map<Long, String> nombresEntrenadores = suscripciones.stream()
                .map(com.sabi.sabi.dto.SuscripcionDTO::getIdEntrenador)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .map(id -> {
                    var ent = entrenadorService.getEntrenadorById(id);
                    return ent != null ? java.util.Map.entry(id, ent.getNombre() != null ? ent.getNombre() : "Entrenador") : null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue));
        model.addAttribute("nombresEntrenadores", nombresEntrenadores);
        java.util.Map<Long, String> nombresClientes = suscripciones.stream()
                .map(com.sabi.sabi.dto.SuscripcionDTO::getIdCliente)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .map(id -> {
                    var cli = clienteService.getClienteById(id);
                    return cli != null ? java.util.Map.entry(id, cli.getNombre() != null ? cli.getNombre() : "Cliente") : null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue));
        model.addAttribute("nombresClientes", nombresClientes);
        // Mapa de rutina activa por cliente (si existe)
        java.util.Map<Long, Long> rutinasActivasClientes = suscripciones.stream()
                .map(com.sabi.sabi.dto.SuscripcionDTO::getIdCliente)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .map(idCliente -> {
                    var r = rutinaService.getRutinaActivaCliente(idCliente);
                    return r != null && r.getIdRutina() != null ? java.util.Map.entry(idCliente, r.getIdRutina()) : null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue));
        model.addAttribute("rutinasActivasClientes", rutinasActivasClientes);
        model.addAttribute("idUsuarioActual", usuario != null ? usuario.getId() : null);
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
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        SuscripcionDTO suscripcionDTO = null;
        try {
            suscripcionDTO = suscripcionService.getSuscripcionById(id);
        } catch (Exception e) {
            // Evitar 500 por id inválido
            redirectAttributes.addFlashAttribute("error", "Suscripción no encontrada.");
            return "redirect:/suscripciones";
        }
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
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam(value = "redirectTo", required = false) String redirectTo,
                                   org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }

        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());

        // Si viene ID (update/cotizar)
        if (suscripcionDTO.getIdSuscripcion() != null) {
            // Preservar idCliente y idEntrenador del registro existente
            SuscripcionDTO existente = suscripcionService.getSuscripcionById(suscripcionDTO.getIdSuscripcion());
            if (existente != null) {
                suscripcionDTO.setIdCliente(existente.getIdCliente());
                suscripcionDTO.setIdEntrenador(existente.getIdEntrenador());

                // VALIDACIÓN: Verificar que el entrenador haya visto el diagnóstico
                // Ahora se valida por duración en semanas en lugar de fechas
                if (suscripcionDTO.getDuracionSemanas() != null && suscripcionDTO.getDuracionSemanas() > 0) {
                    // Verificar que el cliente tenga diagnóstico
                    var diagnostico = clienteService.getDiagnosticoActualByClienteId(existente.getIdCliente());
                    if (diagnostico == null) {
                        redirectAttributes.addFlashAttribute("error",
                            "No puedes cotizar porque el cliente aún no tiene un diagnóstico registrado.");
                        return "redirect:" + (redirectTo != null && !redirectTo.isEmpty() ? redirectTo : "/suscripciones");
                    }

                    // Cambiar a COTIZADA automáticamente
                    suscripcionDTO.setEstadoSuscripcion(com.sabi.sabi.entity.enums.EstadoSuscripcion.COTIZADA);
                } else {
                    // Mantener el estado anterior si no se cumplen condiciones
                    suscripcionDTO.setEstadoSuscripcion(existente.getEstadoSuscripcion());
                }
            }
            suscripcionService.updateSuscripcion(suscripcionDTO.getIdSuscripcion(), suscripcionDTO);
            redirectAttributes.addFlashAttribute("success", "Cotización enviada exitosamente al cliente.");
        } else {
            suscripcionService.createSuscripcion(suscripcionDTO);
        }
        return "redirect:" + (redirectTo != null && !redirectTo.isEmpty() ? redirectTo : "/suscripciones");
    }


    @PostMapping("/suscripciones/pagar/{id}")
    public String pagarSuscripcion(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam(value = "redirectTo", required = false) String redirectTo,
                                   RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        try {
            var suscripcion = suscripcionService.getSuscripcionById(id);
            if (suscripcion != null) {
                suscripcion.setEstadoSuscripcion(com.sabi.sabi.entity.enums.EstadoSuscripcion.ACEPTADA);
                suscripcionService.updateSuscripcion(id, suscripcion);
                redirectAttributes.addFlashAttribute("success", "Pago procesado correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Suscripción no encontrada.");
            }
        } catch (Exception e) {
            // Atrapar excepciones del servicio y evitar 500
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pago: " + e.getMessage());
        }
        return "redirect:" + (redirectTo != null && !redirectTo.isEmpty() ? redirectTo : "/cliente/suscripcion");
    }

    @PostMapping("/suscripciones/rechazar/{id}")
    public String rechazarSuscripcion(@PathVariable Long id,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      @RequestParam(value = "redirectTo", required = false) String redirectTo,
                                      RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        try {
            suscripcionService.cancelarSuscripcion(id);
            redirectAttributes.addFlashAttribute("success", "Suscripción rechazada/cancelada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo rechazar la suscripción: " + e.getMessage());
        }
        return "redirect:" + (redirectTo != null && !redirectTo.isEmpty() ? redirectTo : "/cliente/suscripcion");
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
