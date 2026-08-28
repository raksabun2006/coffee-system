package com.kh.coffeee.feature.auth;

import com.kh.coffeee.config.KeycloakProps;
import com.kh.coffeee.exception.DuplicateResourceException;
import com.kh.coffeee.exception.UnauthorizedException;
import com.kh.coffeee.feature.auth.dto.AuthResponse;
import com.kh.coffeee.feature.auth.dto.LoginRequest;
import com.kh.coffeee.feature.auth.dto.RegisterRequest;
import com.kh.coffeee.feature.auth.dto.UserProfileResponse;
import com.kh.coffeee.utils.Status;
import jakarta.ws.rs.core.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final KeycloakProps keycloakProps;
    private final RestClient restClient;

    private Keycloak createAdminClient() {
        if (keycloakProps.getAdminUsername() == null || keycloakProps.getAdminUsername().isBlank()) {
            throw new IllegalStateException("KEYCLOAK_ADMIN environment variable is missing.");
        }
        if (keycloakProps.getAdminPassword() == null || keycloakProps.getAdminPassword().isBlank()) {
            throw new IllegalStateException("KEYCLOAK_ADMIN_PASSWORD environment variable is missing.");
        }

        return KeycloakBuilder.builder()
                .serverUrl(keycloakProps.getServerUrl())
                .realm("master") // Admin API authenticates against the master realm
                .clientId("admin-cli")
                .grantType(OAuth2Constants.PASSWORD)
                .username(keycloakProps.getAdminUsername())
                .password(keycloakProps.getAdminPassword())
                .build();
    }

    @Override
    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username '" + request.username() + "' is already registered.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email '" + request.email() + "' is already registered.");
        }

        UserRepresentation kcUser = buildUserRepresentation(request);
        String targetRealm = keycloakProps.getRealm();
        String keycloakUserId;

        try (Keycloak keycloakAdmin = createAdminClient()) {
            // 1. Create user in Keycloak realm
            Response response = keycloakAdmin.realm(targetRealm).users().create(kcUser);

            if (response.getStatus() == 409) {
                throw new DuplicateResourceException("User already exists in Keycloak identity directory.");
            }

            if (response.getStatus() != 201) {
                String errorBody = response.readEntity(String.class);
                log.error("Keycloak registration failed (HTTP {}): {}", response.getStatus(), errorBody);
                throw new IllegalStateException("Keycloak registration failed: HTTP " + response.getStatus());
            }

            String locationPath = response.getLocation().getPath();
            keycloakUserId = locationPath.substring(locationPath.lastIndexOf('/') + 1);

            // 2. Assign default realm role (CASHIER)
            try {
                RoleRepresentation defaultRole = keycloakAdmin.realm(targetRealm)
                        .roles()
                        .get("CASHIER")
                        .toRepresentation();

                keycloakAdmin.realm(targetRealm)
                        .users()
                        .get(keycloakUserId)
                        .roles()
                        .realmLevel()
                        .add(Collections.singletonList(defaultRole));

                log.info("Default role 'CASHIER' assigned to Keycloak user ID: {}", keycloakUserId);
            } catch (Exception e) {
                log.warn("Could not automatically assign default realm role: {}", e.getMessage());
            }
        }

        // 3. Save local entity projection into PostgreSQL database
        User user = User.builder()
                .keycloakId(keycloakUserId)
                .username(request.username())
                .email(request.email())
                .displayName(request.displayName())
                .phoneNumber(request.phoneNumber())
                .status(Status.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        return new UserProfileResponse(
                savedUser.getId(),
                savedUser.getKeycloakId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getDisplayName(),
                savedUser.getPhoneNumber(),
                savedUser.getStatus()
        );
    }

    private static @NonNull UserRepresentation buildUserRepresentation(RegisterRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.displayName());
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setRequiredActions(Collections.emptyList());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));
        return user;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                keycloakProps.getServerUrl(), keycloakProps.getRealm());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", keycloakProps.getClientId());

        if (keycloakProps.getClientSecret() != null && !keycloakProps.getClientSecret().isBlank()) {
            formData.add("client_secret", keycloakProps.getClientSecret());
        }

        formData.add("username", request.username());
        formData.add("password", request.password());

        try {
            return restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(AuthResponse.class);
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.BadRequest ex) {
            log.warn("Failed login attempt for user '{}': {}", request.username(), ex.getResponseBodyAsString());
            throw new UnauthorizedException("Invalid username or password.");
        } catch (Exception ex) {
            log.error("Authentication provider error: {}", ex.getMessage(), ex);
            throw new IllegalStateException("Authentication service is temporarily unavailable.");
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                keycloakProps.getServerUrl(), keycloakProps.getRealm());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", keycloakProps.getClientId());

        if (keycloakProps.getClientSecret() != null && !keycloakProps.getClientSecret().isBlank()) {
            formData.add("client_secret", keycloakProps.getClientSecret());
        }

        formData.add("refresh_token", refreshToken);

        try {
            return restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(AuthResponse.class);
        } catch (HttpClientErrorException ex) {
            log.warn("Invalid refresh token attempt: {}", ex.getResponseBodyAsString());
            throw new UnauthorizedException("Refresh token is invalid or expired.");
        } catch (Exception ex) {
            log.error("Refresh token error: {}", ex.getMessage(), ex);
            throw new IllegalStateException("Authentication service is temporarily unavailable.");
        }
    }
}