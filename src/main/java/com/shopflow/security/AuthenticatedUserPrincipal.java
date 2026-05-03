package com.shopflow.security;

public record AuthenticatedUserPrincipal(Long userId, String email, String role) {
}