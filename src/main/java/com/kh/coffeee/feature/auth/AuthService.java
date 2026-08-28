package com.kh.coffeee.feature.auth;


import com.kh.coffeee.feature.auth.dto.AuthResponse;
import com.kh.coffeee.feature.auth.dto.LoginRequest;
import com.kh.coffeee.feature.auth.dto.RegisterRequest;
import com.kh.coffeee.feature.auth.dto.UserProfileResponse;

public interface AuthService {
    UserProfileResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
}
