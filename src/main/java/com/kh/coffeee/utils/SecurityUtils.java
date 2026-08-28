package com.kh.coffeee.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class SecurityUtils {

    private SecurityUtils() {
        // Prevent instantiation
    }

    /**
     * Retrieves the current authenticated JWT principal from the SecurityContext.
     */
    public static Optional<Jwt> getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return Optional.of(jwt);
        }
        return Optional.empty();
    }

    /**
     * Extracts the Keycloak subject/User ID (sub claim).
     */
    public static Optional<String> getCurrentKeycloakUserId() {
        return getCurrentJwt().map(Jwt::getSubject);
    }

    /**
     * Extracts the preferred username from Keycloak token claims.
     */
    public static Optional<String> getCurrentUsername() {
        return getCurrentJwt()
                .map(jwt -> {
                    String username = jwt.getClaimAsString("preferred_username");
                    return (username != null && !username.isBlank()) ? username : jwt.getSubject();
                });
    }

    /**
     * Extracts the email address from Keycloak token claims.
     */
    public static Optional<String> getCurrentEmail() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString("email"));
    }

    /**
     * Extracts all granted roles/authorities of the current authenticated user.
     */
    public static Set<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Collections.emptySet();
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return Collections.emptySet();
        }

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    /**
     * Checks if the currently authenticated user holds a specific role.
     */
    public static boolean hasRole(String role) {
        String roleToCheck = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return getCurrentUserRoles().contains(roleToCheck);
    }
}