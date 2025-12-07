package com.sabi.sabi.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;

/**
 * DataSource configuration for Railway
 * Railway provides DATABASE_URL or individual PG* environment variables
 */
@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String jdbcUrl;
        String username;
        String password;

        // Try DATABASE_URL first (Railway's default format)
        String databaseUrl = System.getenv("DATABASE_URL");

        System.out.println("=== 🔍 RAILWAY DATABASE CONFIGURATION ===");

        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            System.out.println("✅ DATABASE_URL found, parsing...");
            try {
                // Parse DATABASE_URL: postgresql://user:password@host:port/database
                URI dbUri = new URI(databaseUrl.replace("postgresql://", "jdbc:postgresql://"));

                String host = dbUri.getHost();
                int port = dbUri.getPort();
                String path = dbUri.getPath();

                if (port == -1) port = 5432;

                jdbcUrl = String.format("jdbc:postgresql://%s:%d%s", host, port, path);

                String userInfo = dbUri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] credentials = userInfo.split(":");
                    username = credentials[0];
                    password = credentials[1];
                } else {
                    throw new IllegalStateException("DATABASE_URL missing user credentials");
                }

                System.out.println("✅ Parsed from DATABASE_URL");
                System.out.println("   Host: " + host);
                System.out.println("   Port: " + port);
                System.out.println("   Database: " + path);

            } catch (Exception e) {
                System.err.println("❌ Error parsing DATABASE_URL: " + e.getMessage());
                throw new IllegalStateException("Failed to parse DATABASE_URL", e);
            }
        } else {
            // Fallback to individual PG* variables
            System.out.println("📌 DATABASE_URL not found, using PG* variables...");

            String pgHost = System.getenv("PGHOST");
            String pgPort = System.getenv("PGPORT");
            String pgDatabase = System.getenv("PGDATABASE");
            String pgUser = System.getenv("PGUSER");
            String pgPassword = System.getenv("PGPASSWORD");

            System.out.println("PGHOST: " + (pgHost != null ? pgHost : "NOT SET"));
            System.out.println("PGPORT: " + (pgPort != null ? pgPort : "NOT SET"));
            System.out.println("PGDATABASE: " + (pgDatabase != null ? pgDatabase : "NOT SET"));
            System.out.println("PGUSER: " + (pgUser != null ? pgUser : "NOT SET"));

            if (pgHost == null || pgDatabase == null || pgUser == null || pgPassword == null) {
                System.err.println("❌ Missing required PostgreSQL environment variables!");
                throw new IllegalStateException("Missing PostgreSQL environment variables");
            }

            int port = (pgPort != null && !pgPort.isEmpty()) ? Integer.parseInt(pgPort) : 5432;
            jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", pgHost, port, pgDatabase);
            username = pgUser;
            password = pgPassword;
        }

        System.out.println("🔗 JDBC URL: " + jdbcUrl.replaceAll(":[^:@]+@", ":****@"));
        System.out.println("==========================================");

        try {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(jdbcUrl);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setDriverClassName("org.postgresql.Driver");

            // Hikari pool settings optimized for Railway
            dataSource.setMaximumPoolSize(5);
            dataSource.setMinimumIdle(2);
            dataSource.setConnectionTimeout(30000);
            dataSource.setIdleTimeout(600000);
            dataSource.setMaxLifetime(1800000);

            // PostgreSQL specific settings
            dataSource.addDataSourceProperty("ApplicationName", "Sabi-Railway");
            dataSource.addDataSourceProperty("stringtype", "unspecified");

            System.out.println("✅ DataSource configured successfully!");
            return dataSource;

        } catch (Exception e) {
            System.err.println("❌ Error configuring DataSource: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Failed to configure DataSource", e);
        }
    }
}



