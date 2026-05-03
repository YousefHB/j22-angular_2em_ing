package com.shopflow.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration JWT
 * Les propriétés JWT sont lues depuis application.properties
 * via @Value dans JwtService
 */
@Configuration
public class JwtConfig {
    // Les propriétés sont configurées dans application.properties
    // app.jwt.secret
    // app.jwt.expiration
    // app.jwt.refresh-expiration
}