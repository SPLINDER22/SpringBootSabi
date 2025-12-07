package com.sabi.sabi.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuración para crear directorios de uploads en Railway
 */
@Configuration
@Profile("prod")
public class FileStorageConfig {

    @Value("${upload.path:/tmp/uploads/perfiles}")
    private String uploadPath;

    @Value("${upload.diagnosticos.path:/tmp/uploads/diagnosticos}")
    private String uploadDiagnosticosPath;

    @PostConstruct
    public void init() {
        try {
            // Crear directorio de perfiles
            Path perfilesDir = Paths.get(uploadPath);
            if (!Files.exists(perfilesDir)) {
                Files.createDirectories(perfilesDir);
                System.out.println("✅ Directorio de perfiles creado: " + perfilesDir.toAbsolutePath());
            }

            // Crear directorio de diagnósticos
            Path diagnosticosDir = Paths.get(uploadDiagnosticosPath);
            if (!Files.exists(diagnosticosDir)) {
                Files.createDirectories(diagnosticosDir);
                System.out.println("✅ Directorio de diagnósticos creado: " + diagnosticosDir.toAbsolutePath());
            }

            // Crear directorio de certificaciones
            Path certificacionesDir = Paths.get("/tmp/uploads/certificaciones");
            if (!Files.exists(certificacionesDir)) {
                Files.createDirectories(certificacionesDir);
                System.out.println("✅ Directorio de certificaciones creado: " + certificacionesDir.toAbsolutePath());
            }

        } catch (IOException e) {
            System.err.println("❌ Error al crear directorios de uploads: " + e.getMessage());
            // No lanzar excepción para que la app pueda iniciar
        }
    }
}

