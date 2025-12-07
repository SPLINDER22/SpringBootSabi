package com.sabi.sabi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Endpoint de salud para monitoreo en Railway
 */
@RestController
public class HealthController {

    @Autowired
    private DataSource dataSource;

    /**
     * Health check simple
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", "Sabi");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(health);
    }

    /**
     * Health check detallado con verificación de base de datos
     */
    @GetMapping("/health/detailed")
    public ResponseEntity<Map<String, Object>> healthDetailed() {
        Map<String, Object> health = new HashMap<>();
        health.put("application", "Sabi");
        health.put("status", "UP");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));

        // Verificar conexión a base de datos
        try (Connection conn = dataSource.getConnection()) {
            health.put("database", "UP");
            health.put("databaseProductName", conn.getMetaData().getDatabaseProductName());
        } catch (Exception e) {
            health.put("database", "DOWN");
            health.put("databaseError", e.getMessage());
            return ResponseEntity.status(503).body(health);
        }

        return ResponseEntity.ok(health);
    }

    /**
     * Información de la aplicación
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> info = new HashMap<>();
        info.put("application", "Sabi - Salud y Bienestar");
        info.put("version", "1.0.0");
        info.put("description", "Plataforma de gestión de entrenamiento personalizado");
        info.put("profiles", System.getProperty("spring.profiles.active", "default"));
        return ResponseEntity.ok(info);
    }
}

