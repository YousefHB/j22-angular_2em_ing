package com.shopflow.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Profile("prod")
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");
        
        // Parse Render PostgreSQL URL and convert to JDBC format
        if (databaseUrl != null && (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://"))) {
            try {
                URI dbUri = new URI(databaseUrl);
                
                // Extract components
                String host = dbUri.getHost();
                int port = dbUri.getPort();
                String database = dbUri.getPath().substring(1); // Remove leading '/'
                
                // If port is not specified, use default PostgreSQL port
                if (port == -1) {
                    port = 5432;
                }
                
                // Build JDBC URL without credentials
                databaseUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
                
                // Extract username and password from URI if not provided as env vars
                if (username == null && dbUri.getUserInfo() != null) {
                    String[] userInfo = dbUri.getUserInfo().split(":");
                    username = userInfo[0];
                    if (userInfo.length > 1) {
                        password = userInfo[1];
                    }
                }
                
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid DATABASE_URL format", e);
            }
        }
        
        return DataSourceBuilder
                .create()
                .url(databaseUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
