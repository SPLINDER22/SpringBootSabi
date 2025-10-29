package com.sabi.sabi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path:uploads/perfiles}")
    private String uploadPath;

    @Value("${upload.diagnosticos.path:uploads/diagnosticos}")
    private String uploadDiagnosticosPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir fotos de perfil
        registry.addResourceHandler("/uploads/perfiles/**")
                .addResourceLocations("file:" + uploadPath + "/");

        // Servir fotos de diagnósticos
        registry.addResourceHandler("/uploads/diagnosticos/**")
                .addResourceLocations("file:" + uploadDiagnosticosPath + "/");
    }
}

