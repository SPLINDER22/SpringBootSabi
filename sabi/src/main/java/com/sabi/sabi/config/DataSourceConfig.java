package com.sabi.sabi.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;

/**
 * DataSource configuration for Railway with MySQL or PostgreSQL
 */
@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String jdbcUrl = null;
        String username = null;
        String password = null;

        System.out.println("\n=== 🔍 RAILWAY DATABASE CONFIGURATION ===");

        // Print selected environment variables (sanitized)
        System.out.println("Environment snapshot (sensitive values hidden):");
        System.getenv().forEach((key, value) -> {
            String displayValue = value;
            if (key.toUpperCase().contains("PASSWORD") || key.toUpperCase().contains("PASS") || key.toUpperCase().contains("TOKEN") || key.toUpperCase().contains("SECRET")) {
                displayValue = "****";
            }
            System.out.println("  " + key + ": " + displayValue);
        });

        // Prefer DATABASE_URL (Railway Postgres) if present
        String databaseUrlEnv = System.getenv("DATABASE_URL");
        if (databaseUrlEnv != null && !databaseUrlEnv.isBlank()) {
            System.out.println("\n? DATABASE_URL found, parsing...");
            System.out.println("  Original DATABASE_URL: " + databaseUrlEnv.replaceAll(":\\/\\/[^:]+:[^@]+@", "://****:****@"));
            try {
                // Accept formats like: postgresql://user:pass@host:5432/db or postgres://...
                URI dbUri = new URI(databaseUrlEnv);
                String scheme = dbUri.getScheme();
                if (scheme == null || !(scheme.startsWith("postgres") || scheme.startsWith("postgresql"))) {
                    throw new IllegalStateException("DATABASE_URL scheme not recognized as postgres: " + scheme);
                }

                String host = dbUri.getHost();
                int port = dbUri.getPort();
                String path = dbUri.getPath(); // /database
                String userInfo = dbUri.getUserInfo();

                if (host == null || host.isEmpty()) {
                    throw new IllegalStateException("Failed to parse host from DATABASE_URL");
                }
                if (port == -1) {
                    System.out.println("  Port not present in DATABASE_URL, using default 5432");
                    port = 5432;
                }
                if (path == null || path.isEmpty()) {
                    throw new IllegalStateException("Failed to parse database name from DATABASE_URL");
                }

                if (userInfo == null || !userInfo.contains(":")) {
                    throw new IllegalStateException("DATABASE_URL missing user credentials");
                }

                String[] parts = userInfo.split(":", 2);
                username = parts[0];
                password = parts[1];

                jdbcUrl = String.format("jdbc:postgresql://%s:%d%s", host, port, path);

                System.out.println("  URL: " + jdbcUrl.replaceAll(":\\/\\/[^:]+:[^@]+@", "://****:****@"));
                System.out.println("  User: " + username);

            } catch (Exception e) {
                System.err.println("? Error parsing DATABASE_URL: " + e.getMessage());
                System.err.println("  Falling back to MYSQL* env vars if available");
                // continue to fallback
            }
        }

        // If DATABASE_URL parsing didn't produce a jdbcUrl, fallback to MySQL variables (existing logic)
        if (jdbcUrl == null) {
            System.out.println("\n? No valid DATABASE_URL - checking MySQL environment variables...");
            String mysqlHost = System.getenv("MYSQLHOST");
            String mysqlPort = System.getenv("MYSQLPORT");
            String mysqlDatabase = System.getenv("MYSQLDATABASE");
            String mysqlUser = System.getenv("MYSQLUSER");
            String mysqlPassword = System.getenv("MYSQLPASSWORD");

            System.out.println("  MYSQLHOST: " + mysqlHost);
            System.out.println("  MYSQLPORT: " + mysqlPort);
            System.out.println("  MYSQLDATABASE: " + mysqlDatabase);
            System.out.println("  MYSQLUSER: " + mysqlUser);
            System.out.println("  MYSQLPASSWORD: " + (mysqlPassword != null && !mysqlPassword.isEmpty() ? "****" : "NOT SET"));

            if (mysqlHost != null && mysqlDatabase != null && mysqlUser != null && mysqlPassword != null && !mysqlPassword.isEmpty()) {
                int port = 3306;
                if (mysqlPort != null && !mysqlPort.isEmpty()) {
                    try {
                        port = Integer.parseInt(mysqlPort);
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ Invalid MYSQLPORT, using default 3306");
                    }
                }

                jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                        mysqlHost, port, mysqlDatabase);
                username = mysqlUser;
                password = mysqlPassword;

                System.out.println("   Host: " + mysqlHost);
                System.out.println("   Port: " + port);
                System.out.println("   Database: " + mysqlDatabase);
                System.out.println("   User: " + mysqlUser);

            } else {
                // Try MYSQLURL fallback
                String mysqlUrl = System.getenv("MYSQLURL");
                if (mysqlUrl == null || mysqlUrl.isBlank()) {
                    System.err.println("❌ No MySQL configuration found!");
                    throw new IllegalStateException("Missing database configuration: set DATABASE_URL (recommended) or MYSQL* environment variables in Railway.");
                }

                System.out.println("📌 Parsing MYSQLURL: " + mysqlUrl.replaceAll(":\\/\\/[^:]+:[^@]+@", "://****:****@"));
                try {
                    URI dbUri = new URI(mysqlUrl);
                    String host = dbUri.getHost();
                    int port = dbUri.getPort();
                    String path = dbUri.getPath();

                    if (host == null || host.isEmpty()) {
                        throw new IllegalStateException("Invalid MYSQLURL: missing host");
                    }
                    if (port == -1) port = 3306;

                    jdbcUrl = String.format("jdbc:mysql://%s:%d%s?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                            host, port, path);

                    String userInfo = dbUri.getUserInfo();
                    if (userInfo != null && userInfo.contains(":")) {
                        String[] credentials = userInfo.split(":", 2);
                        username = credentials[0];
                        password = credentials[1];
                    } else {
                        throw new IllegalStateException("MYSQLURL missing user credentials (format: mysql://user:password@host:port/database)");
                    }

                    System.out.println("✅ Parsed from MYSQLURL");
                    System.out.println("   Host: " + host);
                    System.out.println("   Port: " + port);
                    System.out.println("   Database: " + path);

                } catch (Exception e) {
                    System.err.println("❌ Error parsing MYSQLURL: " + e.getMessage());
                    e.printStackTrace();
                    throw new IllegalStateException("Failed to parse MYSQLURL: " + e.getMessage(), e);
                }
            }
        }

        // Final validation
        if (jdbcUrl == null || username == null || username.isBlank() || password == null || password.isBlank()) {
            System.err.println("🚨 CRITICAL ERROR: Database configuration incomplete. JDBC URL or credentials missing.");
            throw new IllegalStateException("Database configuration incomplete. Please set DATABASE_URL or MYSQL* environment variables correctly.");
        }

        System.out.println("🔗 JDBC URL (sanitized): " + jdbcUrl.replaceAll(":\\/\\/[^:]+:[^@]+@", "://****:****@"));
        System.out.println("👤 Username: " + username);
        System.out.println("🔐 Password configured: YES (hidden)");
        System.out.println("==========================================");

        try {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(jdbcUrl);
            dataSource.setUsername(username);
            dataSource.setPassword(password);

            // Choose driver by jdbc url
            if (jdbcUrl.startsWith("jdbc:postgresql:")) {
                dataSource.setDriverClassName("org.postgresql.Driver");
            } else {
                dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            }

            // Hikari pool settings optimized for Railway
            dataSource.setMaximumPoolSize(5);
            dataSource.setMinimumIdle(2);
            dataSource.setConnectionTimeout(30000);
            dataSource.setIdleTimeout(600000);
            dataSource.setMaxLifetime(1800000);

            // Common properties
            dataSource.addDataSourceProperty("cachePrepStmts", "true");
            dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
            dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            dataSource.addDataSourceProperty("useServerPrepStmts", "true");

            System.out.println("✅ DataSource configured successfully!");
            return dataSource;

        } catch (Exception e) {
            System.err.println("❌ Error configuring DataSource: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Failed to configure DataSource", e);
        }
    }

    /**
     * Cloudinary configuration for image storage
     * Uses environment variables: CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET
     */
    @Bean
    public Cloudinary cloudinary() {
        System.out.println("=== 📸 CLOUDINARY CONFIGURATION ===");

        String cloudName = System.getenv("CLOUDINARY_CLOUD_NAME");
        String apiKey = System.getenv("CLOUDINARY_API_KEY");
        String apiSecret = System.getenv("CLOUDINARY_API_SECRET");

        System.out.println("Environment Variables:");
        System.out.println("  CLOUDINARY_CLOUD_NAME: " + (cloudName != null ? cloudName : "NOT SET"));
        System.out.println("  CLOUDINARY_API_KEY: " + (apiKey != null ? "SET" : "NOT SET"));
        System.out.println("  CLOUDINARY_API_SECRET: " + (apiSecret != null ? "SET (****)" : "NOT SET"));

        if (cloudName != null && !cloudName.isEmpty() &&
            apiKey != null && !apiKey.isEmpty() &&
            apiSecret != null && !apiSecret.isEmpty()) {

            System.out.println("✅ Cloudinary configured successfully!");
            System.out.println("   Cloud Name: " + cloudName);
            System.out.println("=====================================");

            return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
            ));
        } else {
            System.out.println("⚠️ Cloudinary not configured - using local storage (temporary)");
            System.out.println("   Files will be stored in /tmp and deleted on restart");
            System.out.println("   To enable Cloudinary, set these environment variables:");
            System.out.println("   - CLOUDINARY_CLOUD_NAME");
            System.out.println("   - CLOUDINARY_API_KEY");
            System.out.println("   - CLOUDINARY_API_SECRET");
            System.out.println("=====================================");
            return null; // Will fallback to local storage
        }
    }
}
