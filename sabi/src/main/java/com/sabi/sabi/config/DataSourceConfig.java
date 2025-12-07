package com.sabi.sabi.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * DataSource configuration for Railway
 * Railway provides DATABASE_URL in format: postgresql://user:pass@host:port/db
 * Spring Boot needs: jdbc:postgresql://host:port/db with separate user/pass
 */
@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        // Try alternative environment variables used by Railway
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            String pgHost = System.getenv("PGHOST");
            String pgPort = System.getenv("PGPORT");
            String pgDatabase = System.getenv("PGDATABASE");
            String pgUser = System.getenv("PGUSER");
            String pgPassword = System.getenv("PGPASSWORD");

            if (pgHost != null && pgDatabase != null && pgUser != null && pgPassword != null) {
                int port = (pgPort != null) ? Integer.parseInt(pgPort) : 5432;
                databaseUrl = String.format("postgresql://%s:%s@%s:%d/%s",
                    pgUser, pgPassword, pgHost, port, pgDatabase);
                System.out.println("✅ Built DATABASE_URL from PG* environment variables");
            } else {
                System.err.println("❌ DATABASE_URL not set and PG* variables incomplete:");
                System.err.println("   PGHOST: " + (pgHost != null ? "SET" : "NOT SET"));
                System.err.println("   PGPORT: " + (pgPort != null ? "SET" : "NOT SET"));
                System.err.println("   PGDATABASE: " + (pgDatabase != null ? "SET" : "NOT SET"));
                System.err.println("   PGUSER: " + (pgUser != null ? "SET" : "NOT SET"));
                System.err.println("   PGPASSWORD: " + (pgPassword != null ? "SET" : "NOT SET"));
                throw new IllegalStateException("❌ DATABASE_URL environment variable is not set and PG* variables are incomplete");
            }
        }

        System.out.println("🔍 Original DATABASE_URL: " + databaseUrl.replaceAll(":[^:@]+@", ":****@"));

        try {
            String username;
            String password;
            String host;
            int port;
            String database;
            String jdbcUrl;

            // Check if DATABASE_URL already has jdbc: prefix
            if (databaseUrl.startsWith("jdbc:")) {
                // Format: jdbc:postgresql://host:port/db?user=xxx&password=xxx
                jdbcUrl = databaseUrl;

                // Extract user and password from URL parameters
                if (databaseUrl.contains("?")) {
                    String params = databaseUrl.split("\\?")[1];
                    username = extractParam(params, "user");
                    password = extractParam(params, "password");
                } else {
                    // Try from properties if not in URL
                    username = System.getenv("PGUSER");
                    password = System.getenv("PGPASSWORD");

                    if (username == null || password == null) {
                        username = "postgres";
                        password = "";
                    }
                }

                URI dbUri = new URI(databaseUrl.substring(5)); // Remove "jdbc:"
                host = dbUri.getHost();
                port = dbUri.getPort();
                database = dbUri.getPath().substring(1);

            } else {
                // Format: postgresql://user:pass@host:port/db or postgres://...
                String urlToParse = databaseUrl;
                if (databaseUrl.startsWith("postgres://")) {
                    urlToParse = databaseUrl.replace("postgres://", "postgresql://");
                }

                URI dbUri = new URI(urlToParse);

                // Extract credentials
                String userInfo = dbUri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] credentials = userInfo.split(":", 2);
                    username = credentials[0];
                    password = credentials[1];
                } else {
                    // Fallback to environment variables
                    username = System.getenv("PGUSER");
                    password = System.getenv("PGPASSWORD");

                    if (username == null) username = "postgres";
                    if (password == null) password = "";
                }

                host = dbUri.getHost();
                port = dbUri.getPort();
                if (port == -1) port = 5432; // Default PostgreSQL port

                String path = dbUri.getPath();
                database = (path != null && path.length() > 1) ? path.substring(1) : "railway";

                // Validate parsed values
                if (host == null || host.isEmpty()) {
                    throw new IllegalStateException("Failed to parse host from DATABASE_URL");
                }
                if (database == null || database.isEmpty()) {
                    throw new IllegalStateException("Failed to parse database name from DATABASE_URL");
                }
                if (username == null || username.isEmpty()) {
                    throw new IllegalStateException("Failed to parse username from DATABASE_URL");
                }

                // Build JDBC URL
                jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
            }

            System.out.println("✅ DATABASE_URL parsed successfully:");
            System.out.println("   Host: " + host);
            System.out.println("   Port: " + port);
            System.out.println("   Database: " + database);
            System.out.println("   User: " + username);
            System.out.println("   JDBC URL: " + jdbcUrl);

            // Create DataSource
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

            return dataSource;

        } catch (Exception e) {
            System.err.println("❌ Error parsing DATABASE_URL: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Failed to configure DataSource from DATABASE_URL", e);
        }
    }

    /**
     * Extract parameter from URL query string
     */
    private String extractParam(String params, String key) {
        for (String param : params.split("&")) {
            String[] pair = param.split("=", 2);
            if (pair.length == 2 && pair[0].equals(key)) {
                return pair[1];
            }
        }
        return null;
    }
}



