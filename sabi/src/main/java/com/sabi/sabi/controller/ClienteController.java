package com.sabi.sabi.controller;

import com.sabi.sabi.dto.DiagnosticoDTO;
import com.sabi.sabi.dto.EntrenadorDTO;
import com.sabi.sabi.dto.RutinaDTO;
import com.sabi.sabi.service.ClienteService;
import com.sabi.sabi.service.DiagnosticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.sabi.sabi.security.CustomUserDetails;

import java.util.List;

@Controller
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private DiagnosticoService diagnosticoService;

    @GetMapping("/cliente/dashboard")
    public String clienteDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();
        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        boolean tieneDiagnostico = diagnosticoActual != null;
        model.addAttribute("tieneDiagnostico", tieneDiagnostico);
        model.addAttribute("diagnosticoActual", diagnosticoActual);
        return "cliente/dashboard"; // templates/cliente/dashboard.html
    }

    @GetMapping("/cliente/rutinas")
    public String clienteRutinas(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();
        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        boolean tieneDiagnostico = diagnosticoActual != null;
        List<RutinaDTO> rutinas = clienteService.getRutinasByClienteId(clienteId);
        model.addAttribute("tieneRutinas", !rutinas.isEmpty());
        model.addAttribute("rutinas", rutinas);
        model.addAttribute("tieneDiagnostico", tieneDiagnostico);
        return "cliente/rutinas";
    }

    @GetMapping("/cliente/listaEntrenadores")
    public String clienteListaEntrenadores(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();
        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        boolean tieneDiagnostico = diagnosticoActual != null;
        List<EntrenadorDTO> entrenadores = clienteService.getAllEntrenadores();
        model.addAttribute("entrenadores", entrenadores);
        model.addAttribute("tieneDiagnostico", tieneDiagnostico);
        return "cliente/listaEntrenadores";
    }

    @GetMapping("/cliente/diagnostico/historial")
    public String clienteDiagnosticoHistorial(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();
        List<DiagnosticoDTO> diagnosticos = clienteService.getHistorialDiagnosticosByClienteId(clienteId);
        model.addAttribute("diagnosticos", diagnosticos);
        return "cliente/diagnostico-historial";
    }

    @GetMapping("/cliente/diagnostico/crear")
    public String mostrarFormularioDiagnostico(Model model) {
        model.addAttribute("diagnostico", new DiagnosticoDTO());
        return "cliente/diagnostico-form";
    }

    @PostMapping("/cliente/diagnostico/guardar")
    public String guardarDiagnostico(@ModelAttribute("diagnostico") @Valid DiagnosticoDTO diagnosticoDTO,
                                     BindingResult result,
                                     Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Por favor corrige los errores del formulario.");
            return "cliente/diagnostico-form";
        }
        // Obtener el cliente autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long clienteId = userDetails.getUsuario().getId();
        diagnosticoDTO.setIdCliente(clienteId);

        // Guardar diagnóstico usando DiagnosticoService
        try {
            diagnosticoService.createDiagnostico(diagnosticoDTO);
            model.addAttribute("success", "Diagnóstico guardado correctamente.");
            return "redirect:/cliente/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar el diagnóstico: " + e.getMessage());
            return "cliente/diagnostico-form";
        }
    }
}
