package com.sabi.sabi.controller;

import com.sabi.sabi.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping("/cliente/dashboard")
    public String clienteDashboard(Model model) {
        model.addAttribute("tieneDiagnostico", false);
        model.addAttribute("diagnosticoActual", null);
        return "cliente/dashboard"; // templates/cliente/dashboard.html
    }

    @GetMapping("/cliente/rutinas")
    public String clienteRutinas(Model model) {
        model.addAttribute("tieneRutinas", false);
        model.addAttribute("rutinas", List.of());
        model.addAttribute("tieneDiagnostico", false);
        return "cliente/rutinas";
    }

    @GetMapping("/cliente/listaEntrenadores")
    public String clienteListaEntrenadores(Model model) {
        model.addAttribute("entrenadores", List.of());
        model.addAttribute("tieneDiagnostico", false);
        return "cliente/listaEntrenadores";
    }

    @GetMapping("/cliente/diagnostico/historial")
    public String clienteDiagnosticoHistorial(Model model) {
        model.addAttribute("diagnosticos", List.of());
        return "cliente/diagnostico-historial";
    }
}
