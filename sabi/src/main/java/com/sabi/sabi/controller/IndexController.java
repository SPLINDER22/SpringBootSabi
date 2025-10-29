package com.sabi.sabi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(Authentication authentication) {
        // Si ya está autenticado, redirigir al dashboard correspondiente
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                return "redirect:/cliente/dashboard";
            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ENTRENADOR"))) {
                return "redirect:/entrenador/dashboard";
            }
        }
        return "index";
    }

    @GetMapping("/index")
    public String indexPage(Authentication authentication) {
        return index(authentication);
    }
}

