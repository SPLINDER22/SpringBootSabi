package com.sabi.sabi.controller;

import com.sabi.sabi.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping("/cliente/dashboard")
    public String clienteDashboard() {
        return "cliente/dashboard"; // templates/cliente/dashboard.html
    }
}
