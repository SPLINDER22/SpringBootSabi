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

        // Try MYSQLURL first (Railway's MySQL format)
        String mysqlUrl = System.getenv("MYSQLURL");

        if (mysqlUrl != null && !mysqlUrl.isEmpty()) {
            System.out.println("✅ MYSQLURL found, parsing...");
            try {
                // Parse MYSQLURL: mysql://user:password@host:port/database
                URI dbUri = new URI(mysqlUrl);

                String host = dbUri.getHost();
                int port = dbUri.getPort();
                String path = dbUri.getPath();

                if (port == -1) port = 3306;

                // Build JDBC URL with proper MySQL parameters
                jdbcUrl = String.format("jdbc:mysql://%s:%d%s?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                    host, port, path);

                String userInfo = dbUri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] credentials = userInfo.split(":", 2);
                    username = credentials[0];
                    password = credentials[1];
                } else {
                    throw new IllegalStateException("MYSQLURL missing user credentials");
                }

                System.out.println("✅ Parsed from MYSQLURL");
                System.out.println("   Host: " + host);
                System.out.println("   Port: " + port);
                System.out.println("   Database: " + path);

            } catch (Exception e) {
                System.err.println("❌ Error parsing MYSQLURL: " + e.getMessage());
                e.printStackTrace();
                throw new IllegalStateException("Failed to parse MYSQLURL", e);
            }
        } else {
            // Fallback to individual MYSQL* variables
            System.out.println("📌 MYSQLURL not found, using MYSQL* variables...");

            String mysqlHost = System.getenv("MYSQLHOST");
            String mysqlPort = System.getenv("MYSQLPORT");
            String mysqlDatabase = System.getenv("MYSQLDATABASE");
            String mysqlUser = System.getenv("MYSQLUSER");
            String mysqlPassword = System.getenv("MYSQLPASSWORD");

            System.out.println("MYSQLHOST: " + (mysqlHost != null ? mysqlHost : "NOT SET"));
            System.out.println("MYSQLPORT: " + (mysqlPort != null ? mysqlPort : "NOT SET"));
            System.out.println("MYSQLDATABASE: " + (mysqlDatabase != null ? mysqlDatabase : "NOT SET"));
            System.out.println("MYSQLUSER: " + (mysqlUser != null ? mysqlUser : "NOT SET"));

            if (mysqlHost == null || mysqlDatabase == null || mysqlUser == null || mysqlPassword == null) {
                System.err.println("❌ Missing required MySQL environment variables!");
                throw new IllegalStateException("Missing MySQL environment variables (MYSQLHOST, MYSQLDATABASE, MYSQLUSER, MYSQLPASSWORD)");
            }

            int port = (mysqlPort != null && !mysqlPort.isEmpty()) ? Integer.parseInt(mysqlPort) : 3306;
            jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                mysqlHost, port, mysqlDatabase);
            username = mysqlUser;
            password = mysqlPassword;
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



