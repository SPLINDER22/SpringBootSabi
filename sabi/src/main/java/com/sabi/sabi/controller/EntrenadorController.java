package com.sabi.sabi.controller;

import com.sabi.sabi.service.EntrenadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EntrenadorController {
    @Autowired
    private EntrenadorService entrenadorService;

    @GetMapping("/entrenador/dashboard")
    public String entrenadorDashboard() {
        return "entrenador/dashboard"; // templates/entrenador/dashboard.html
    }
}
