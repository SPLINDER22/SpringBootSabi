package com.sabi.sabi.config;

import com.sabi.sabi.entity.Cliente;
import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.enums.Rol;
import com.sabi.sabi.repository.DiagnosticoRepository;
import com.sabi.sabi.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class DiagnosticoInterceptor implements HandlerInterceptor {

    private final DiagnosticoRepository diagnosticoRepository;

    // Rutas que NO requieren diagnóstico
    private static final List<String> RUTAS_EXCLUIDAS = Arrays.asList(
        "/cliente/diagnostico",
        "/cliente/dashboard",
        "/perfil",
        "/logout",
        "/css",
        "/js",
        "/img",
        "/vendor",
        "/error",
        "/auth"
    );

    public DiagnosticoInterceptor(DiagnosticoRepository diagnosticoRepository) {
        this.diagnosticoRepository = diagnosticoRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Usuario usuario = userDetails.getUsuario();

            // Solo aplicar para CLIENTES
            if (usuario.getRol() == Rol.CLIENTE) {
                Cliente cliente = (Cliente) usuario;
                String requestURI = request.getRequestURI();

                // Verificar si la ruta está excluida
                boolean rutaExcluida = RUTAS_EXCLUIDAS.stream()
                    .anyMatch(requestURI::startsWith);

                if (!rutaExcluida) {
                    // Verificar si tiene diagnóstico activo
                    boolean tieneDiagnostico = !diagnosticoRepository
                        .findByClienteIdAndEstadoTrue(cliente.getId())
                        .isEmpty();

                    if (!tieneDiagnostico) {
                        // Redirigir al dashboard del cliente donde se mostrará el modal
                        response.sendRedirect("/cliente/dashboard");
                        return false;
                    }
                }
            }
        }

        return true;
    }
}

