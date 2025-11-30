package com.sabi.sabi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final DiagnosticoInterceptor diagnosticoInterceptor;

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
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}

