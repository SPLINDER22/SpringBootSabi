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
 * DataSource configuration for Railway with MySQL
 * Railway provides MYSQLURL and MYSQL* environment variables
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

        System.out.println("=== 🔍 RAILWAY MySQL DATABASE CONFIGURATION ===");

        // Print all available environment variables for debugging
        System.out.println("Environment Variables:");
        System.out.println("  MYSQLURL: " + (System.getenv("MYSQLURL") != null ? "SET" : "NOT SET"));
        System.out.println("  MYSQLHOST: " + System.getenv("MYSQLHOST"));
        System.out.println("  MYSQLPORT: " + System.getenv("MYSQLPORT"));
        System.out.println("  MYSQLDATABASE: " + System.getenv("MYSQLDATABASE"));
        System.out.println("  MYSQLUSER: " + System.getenv("MYSQLUSER"));
        System.out.println("  MYSQLPASSWORD: " + (System.getenv("MYSQLPASSWORD") != null ? "****" : "NOT SET"));

        // Try individual MYSQL* variables first (more reliable)
        String mysqlHost = System.getenv("MYSQLHOST");
        String mysqlPort = System.getenv("MYSQLPORT");
        String mysqlDatabase = System.getenv("MYSQLDATABASE");
        String mysqlUser = System.getenv("MYSQLUSER");
        String mysqlPassword = System.getenv("MYSQLPASSWORD");

        if (mysqlHost != null && mysqlDatabase != null && mysqlUser != null && mysqlPassword != null) {
            System.out.println("✅ Using individual MYSQL* variables");

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
            // Fallback to MYSQLURL
            String mysqlUrl = System.getenv("MYSQLURL");

            if (mysqlUrl == null || mysqlUrl.isEmpty()) {
                System.err.println("❌ No MySQL configuration found!");
                System.err.println("Missing: MYSQLHOST=" + mysqlHost + ", MYSQLDATABASE=" + mysqlDatabase +
                                 ", MYSQLUSER=" + mysqlUser + ", MYSQLPASSWORD=" + (mysqlPassword != null ? "SET" : "NULL"));
                throw new IllegalStateException("Missing MySQL environment variables. Please configure MySQL in Railway.");
            }

            System.out.println("📌 Parsing MYSQLURL: " + mysqlUrl.replaceAll(":[^:@]+@", ":****@"));

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

        System.out.println("🔗 JDBC URL: " + jdbcUrl.replaceAll(":[^:@]+@", ":****@"));
        System.out.println("==========================================");

        try {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(jdbcUrl);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // Hikari pool settings optimized for Railway
            dataSource.setMaximumPoolSize(5);
            dataSource.setMinimumIdle(2);
            dataSource.setConnectionTimeout(30000);
            dataSource.setIdleTimeout(600000);
            dataSource.setMaxLifetime(1800000);

            // MySQL specific settings
            dataSource.addDataSourceProperty("cachePrepStmts", "true");
            dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
            dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            dataSource.addDataSourceProperty("useServerPrepStmts", "true");
            dataSource.addDataSourceProperty("useLocalSessionState", "true");
            dataSource.addDataSourceProperty("rewriteBatchedStatements", "true");
            dataSource.addDataSourceProperty("cacheResultSetMetadata", "true");
            dataSource.addDataSourceProperty("cacheServerConfiguration", "true");
            dataSource.addDataSourceProperty("elideSetAutoCommits", "true");
            dataSource.addDataSourceProperty("maintainTimeStats", "false");

            System.out.println("✅ MySQL DataSource configured successfully!");
            return dataSource;

        } catch (Exception e) {
            System.err.println("❌ Error configuring MySQL DataSource: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Failed to configure MySQL DataSource", e);
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



