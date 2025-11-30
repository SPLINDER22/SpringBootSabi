package com.sabi.sabi.config;

import com.sabi.sabi.entity.enums.Rol;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectURL = "/"; // valor por defecto

        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            redirectURL = "/admin/dashboard";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            redirectURL = "/cliente/dashboard";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ENTRENADOR"))) {
            redirectURL = "/entrenador/dashboard";
        }

        response.sendRedirect(redirectURL);
    }
}
