package com.sabi.sabi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final DiagnosticoInterceptor diagnosticoInterceptor;

    @Value("${upload.path:uploads/perfiles}")
    private String uploadPath;

    @Value("${upload.diagnosticos.path:uploads/diagnosticos}")
    private String uploadDiagnosticosPath;

    public WebMvcConfig(DiagnosticoInterceptor diagnosticoInterceptor) {
        this.diagnosticoInterceptor = diagnosticoInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ⚠️ INTERCEPTOR DESACTIVADO TEMPORALMENTE
        // El modal obligatorio de diagnóstico se muestra en el dashboard
        // No necesitamos bloquear todas las rutas, solo mostrar el modal

        /*
        registry.addInterceptor(diagnosticoInterceptor)
                .addPathPatterns("/cliente/**")
                .excludePathPatterns(
                    "/cliente/diagnostico/**",
                    "/cliente/dashboard",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/vendor/**"
                );
        */
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos de uploads (perfiles, certificaciones, diagnósticos)
        // Soporta tanto rutas relativas (desarrollo) como absolutas (producción)
        String uploadsLocation = uploadPath.startsWith("/")
            ? "file:" + Paths.get(uploadPath).getParent() + "/"
            : "file:" + Paths.get("uploads").toAbsolutePath() + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsLocation);
    }
}



