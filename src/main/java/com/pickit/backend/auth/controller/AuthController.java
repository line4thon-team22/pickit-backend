package com.pickit.backend.auth.controller;

import com.pickit.backend.auth.dto.request.GoogleLoginRequest;
import com.pickit.backend.auth.dto.request.LoginRequest;
import com.pickit.backend.auth.dto.request.LogoutRequest;
import com.pickit.backend.auth.dto.request.RefreshTokenRequest;
import com.pickit.backend.auth.dto.request.SignupRequest;
import com.pickit.backend.auth.dto.response.GoogleLoginResponse;
import com.pickit.backend.auth.dto.response.LoginResponse;
import com.pickit.backend.auth.dto.response.RefreshTokenResponse;
import com.pickit.backend.auth.dto.response.SignupResponse;
import com.pickit.backend.auth.service.AuthService;
import com.pickit.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/google")
    public ApiResponse<GoogleLoginResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        GoogleLoginResponse response = authService.googleLogin(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refresh(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ApiResponse.success(null);
    }
}
