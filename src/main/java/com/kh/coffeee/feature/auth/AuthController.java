package com.kh.coffeee.feature.auth;

import com.kh.coffeee.feature.auth.dto.AuthResponse;
import com.kh.coffeee.feature.auth.dto.LoginRequest;
import com.kh.coffeee.feature.auth.dto.RegisterRequest;
import com.kh.coffeee.feature.auth.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication & Registration APIs")
@SecurityRequirements(value = {})
public class AuthController {

    private final AuthService authService;

    @SecurityRequirements(value = {})
    @PostMapping("/register")
    public ResponseEntity<UserProfileResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }
    @SecurityRequirements(value = {})
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @SecurityRequirements(value = {})
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam("refresh_token") String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}