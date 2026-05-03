package com.shopflow.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;

    /**
     * Générer un Access Token (durée courte - 24 heures par défaut)
     */
    public String generateAccessToken(Long userId, String email, String role) {
        return buildToken(userId, email, role, jwtExpirationMs);
    }

    /**
     * Générer un Refresh Token (durée plus longue - 7 jours par défaut)
     */
    public String generateRefreshToken(Long userId, String email) {
        return buildToken(userId, email, "", refreshTokenExpirationMs);
    }

    /**
     * Construire un token JWT
     */
    private String buildToken(Long userId, String email, String role, long expirationTime) {
        Algorithm algorithm = Algorithm.HMAC512(jwtSecret);

        return JWT.create()
                .withSubject(email)
                .withClaim("userId", userId)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(algorithm);
    }

    /**
     * Extraire les claims d'un token JWT
     */
    public DecodedJWT getClaimsFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
            return JWT.require(algorithm)
                    .build()
                    .verify(token);
        } catch (JWTDecodeException e) {
            log.error("Erreur lors du parsing du token JWT: {}", e.getMessage());
            throw new IllegalArgumentException("Token JWT invalide");
        } catch (Exception e) {
            log.error("Erreur lors de la vérification du token JWT", e);
            throw new IllegalArgumentException("Token invalide");
        }
    }

    /**
     * Extraire l'ID utilisateur d'un token
     */
    public Long getUserIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = getClaimsFromToken(token);
            Claim claim = decodedJWT.getClaim("userId");
            return claim.asLong();
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction du userId du token", e);
            throw new IllegalArgumentException("Impossible d'extraire l'userId du token");
        }
    }

    /**
     * Extraire l'email (subject) d'un token
     */
    public String getEmailFromToken(String token) {
        try {
            DecodedJWT decodedJWT = getClaimsFromToken(token);
            return decodedJWT.getSubject();
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction de l'email du token", e);
            throw new IllegalArgumentException("Impossible d'extraire l'email du token");
        }
    }

    /**
     * Extraire le rôle d'un token
     */
    public String getRoleFromToken(String token) {
        try {
            DecodedJWT decodedJWT = getClaimsFromToken(token);
            Claim claim = decodedJWT.getClaim("role");
            return claim.asString();
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction du rôle du token", e);
            throw new IllegalArgumentException("Impossible d'extraire le rôle du token");
        }
    }

    /**
     * Vérifier qu'un token est valide
     * Un token est valide si:
     * 1. La signature est correcte
     * 2. Il n'a pas expiré
     */
    public boolean isTokenValid(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            log.debug("Token invalide: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Vérifier qu'un token n'a pas expiré
     */
    public boolean isTokenNotExpired(String token) {
        try {
            DecodedJWT decodedJWT = getClaimsFromToken(token);
            return decodedJWT.getExpiresAt().after(new Date());
        } catch (Exception e) {
            log.debug("Erreur lors de la vérification de l'expiration du token", e);
            return false;
        }
    }
}