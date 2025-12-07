package com.sabi.sabi.config;

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
}



