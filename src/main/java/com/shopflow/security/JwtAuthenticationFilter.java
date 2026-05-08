package com.shopflow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Extraire le JWT du header Authorization
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtService.isTokenValid(jwt)) {
                // Extraire l'email et l'ID du JWT
                String email = jwtService.getEmailFromToken(jwt);
                Long userId = jwtService.getUserIdFromToken(jwt);
                String role = jwtService.getRoleFromToken(jwt);

                AuthenticatedUserPrincipal principal = new AuthenticatedUserPrincipal(userId, email, role);
                String authority = StringUtils.hasText(role) ? "ROLE_" + role : "ROLE_USER";

                // Créer une authentification
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(authority)));
                authentication.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));

                // Définir l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT Token validé pour l'utilisateur: {}", email);
            }
        } catch (Exception e) {
            log.error("Impossible de traiter le JWT Token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extraire le JWT du header Authorization
     * Format: Bearer <token>
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7).trim();
            // Tolerate accidental "Bearer <token>" pasted into Swagger Authorize field.
            if (token.startsWith("Bearer ")) {
                token = token.substring(7).trim();
            }
            return token;
        }
        return null;
    }
}