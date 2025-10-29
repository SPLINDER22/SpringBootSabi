package com.sabi.sabi.controller;

import com.sabi.sabi.dto.DiagnosticoDTO;
import com.sabi.sabi.service.DiagnosticoService;
import com.sabi.sabi.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/diagnostico")
public class DiagnosticoController {

    @Autowired
    private DiagnosticoService diagnosticoService;
    @Autowired
    private ClienteService clienteService;

    @GetMapping("/cliente")
    public String diagnosticoCliente(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long clienteId = clienteService.getClienteByEmail(userDetails.getUsername()).getId();
        DiagnosticoDTO diagnosticoActual = clienteService.getDiagnosticoActualByClienteId(clienteId);
        List<DiagnosticoDTO> historial = clienteService.getHistorialDiagnosticosByClienteId(clienteId);
        model.addAttribute("diagnosticoActual", diagnosticoActual);
        model.addAttribute("historial", historial);
        return "cliente/diagnostico-form";
    }

    @PostMapping("/crear")
    public String crearDiagnostico(@AuthenticationPrincipal UserDetails userDetails,
                                   @ModelAttribute DiagnosticoDTO diagnosticoDTO) {
        Long clienteId = clienteService.getClienteByEmail(userDetails.getUsername()).getId();
        diagnosticoDTO.setIdCliente(clienteId);
        diagnosticoDTO.setFecha(java.time.LocalDate.now()); // Asignar solo la fecha actual

        // Guardar el diagnóstico
        diagnosticoService.createDiagnostico(diagnosticoDTO);

        // Si el objetivo fue proporcionado, actualizarlo también en el perfil del cliente
        if (diagnosticoDTO.getObjetivo() != null && !diagnosticoDTO.getObjetivo().isEmpty()) {
            com.sabi.sabi.dto.ClienteDTO clienteDTO = clienteService.getClienteById(clienteId);
            clienteDTO.setObjetivo(diagnosticoDTO.getObjetivo());
            clienteService.updateCliente(clienteId, clienteDTO);
        }

        return "redirect:/cliente/dashboard";
    }

    @GetMapping("/detalle/{id}")
    @ResponseBody
    public DiagnosticoDTO detalleDiagnostico(@PathVariable Long id) {
        return diagnosticoService.getDiagnosticoById(id);
    }

    @GetMapping("/historial")
    public String historialDiagnosticos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long clienteId = clienteService.getClienteByEmail(userDetails.getUsername()).getId();
        List<DiagnosticoDTO> historial = clienteService.getHistorialDiagnosticosByClienteId(clienteId);
        model.addAttribute("historial", historial);
        return "cliente/diagnostico-historial";
    }
}
