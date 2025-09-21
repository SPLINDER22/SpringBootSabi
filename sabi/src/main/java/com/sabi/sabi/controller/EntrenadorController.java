package com.sabi.sabi.controller;

import com.sabi.sabi.service.EntrenadorService;
import com.sabi.sabi.service.SuscripcionService;
import com.sabi.sabi.service.ReportePdfService;
import com.sabi.sabi.service.UsuarioService;
import com.sabi.sabi.dto.SuscripcionDTO;
import com.sabi.sabi.entity.enums.EstadoSuscripcion;
import com.sabi.sabi.service.ClienteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class EntrenadorController {
    @Autowired
    private EntrenadorService entrenadorService;

    @Autowired
    private SuscripcionService suscripcionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ReportePdfService reportePdfService;

    @GetMapping("/entrenador/dashboard")
    public String entrenadorDashboard() {
        return "entrenador/dashboard";
    }

    @GetMapping("/entrenadores")
    public String getAllActiveEntrenadores(Model model) {
        model.addAttribute("entrenadores", entrenadorService.getAllActiveEntrenadores());
        return "cliente/listaEntrenadores";
    }

    @GetMapping("/entrenador/suscripciones")
    public String listarSuscripcionesEntrenador(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        var todas = suscripcionService.getAllSuscripciones();
    var suscripciones = todas.stream()
        .filter(s -> s.getIdEntrenador() != null && s.getIdEntrenador().equals(usuario.getId()))
        .filter(s -> s.getEstadoSuscripcion() != com.sabi.sabi.entity.enums.EstadoSuscripcion.RECHAZADA)
                .collect(Collectors.toList());
        model.addAttribute("suscripciones", suscripciones);
        Map<Long, String> nombresEntrenadores = suscripciones.stream()
                .map(SuscripcionDTO::getIdEntrenador)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(id -> id, id -> entrenadorService.getEntrenadorById(id).getNombre()));
        model.addAttribute("nombresEntrenadores", nombresEntrenadores);
        Map<Long, String> nombresClientes = suscripciones.stream()
                .map(SuscripcionDTO::getIdCliente)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(id -> id, id -> clienteService.getClienteById(id).getNombre()));
        model.addAttribute("nombresClientes", nombresClientes);
        return "entrenador/suscripciones";
    }

    @GetMapping("/entrenador/suscripciones/historial")
    public String historialSuscripcionesEntrenador(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        var todas = suscripcionService.getAllSuscripciones();
        var suscripciones = todas.stream()
                .filter(s -> s.getIdEntrenador() != null && s.getIdEntrenador().equals(usuario.getId()))
                .filter(s -> s.getEstadoSuscripcion() == com.sabi.sabi.entity.enums.EstadoSuscripcion.RECHAZADA)
                .collect(Collectors.toList());
        model.addAttribute("suscripciones", suscripciones);
        Map<Long, String> nombresEntrenadores = suscripciones.stream()
                .map(SuscripcionDTO::getIdEntrenador)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(id -> id, id -> entrenadorService.getEntrenadorById(id).getNombre()));
        model.addAttribute("nombresEntrenadores", nombresEntrenadores);
        Map<Long, String> nombresClientes = suscripciones.stream()
                .map(SuscripcionDTO::getIdCliente)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(id -> id, id -> clienteService.getClienteById(id).getNombre()));
        model.addAttribute("nombresClientes", nombresClientes);
        return "entrenador/suscripciones-historial";
    }

    @GetMapping("/entrenador/suscripciones/reporte.pdf")
    public org.springframework.http.ResponseEntity<byte[]> descargarReporteSuscripciones() {
        var baos = reportePdfService.generarReporteSuscripciones();
        byte[] pdf = baos.toByteArray();
        var headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.set("Content-Disposition", "attachment; filename=Reporte_Suscripciones.pdf");
        return org.springframework.http.ResponseEntity.ok().headers(headers).body(pdf);
    }

    @org.springframework.web.bind.annotation.PostMapping("/entrenadores/solicitar/{idEntrenador}")
    public String solicitarEntrenador(@org.springframework.web.bind.annotation.PathVariable Long idEntrenador,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        var usuario = usuarioService.obtenerPorEmail(userDetails.getUsername());
        // Evitar múltiples suscripciones activas
        if (suscripcionService.existsSuscripcionActivaByClienteId(usuario.getId())) {
            redirectAttributes.addFlashAttribute("error", "No puedes solicitar un entrenador porque ya tienes una suscripción activa con otro entrenador.");
            return "redirect:/cliente/suscripcion";
        }
        // Crear la suscripción asociando cliente y entrenador
        SuscripcionDTO suscripcionDTO = new SuscripcionDTO();
        suscripcionDTO.setIdCliente(usuario.getId());
        suscripcionDTO.setIdEntrenador(idEntrenador);
        suscripcionDTO.setEstadoSuscripcion(EstadoSuscripcion.PENDIENTE);
        suscripcionService.createSuscripcion(suscripcionDTO);
        return "redirect:/cliente/suscripcion";
    }
}
