package com.sabi.sabi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

/**
 * Configuración de DataSource para Railway
 * Railway proporciona DATABASE_URL en formato: postgresql://user:pass@host:port/db
 * Spring Boot necesita: jdbc:postgresql://user:pass@host:port/db
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && !databaseUrl.startsWith("jdbc:")) {
            // Railway format: postgresql://...
            // Convert to: jdbc:postgresql://...
            databaseUrl = "jdbc:" + databaseUrl;
            System.out.println("✅ DATABASE_URL convertida a formato JDBC: " +
                databaseUrl.replaceAll(":[^:@]+@", ":****@"));
        }

        return DataSourceBuilder
                .create()
                .url(databaseUrl != null ? databaseUrl : "jdbc:postgresql://localhost:5432/sabi")
                .build();
    }
}

