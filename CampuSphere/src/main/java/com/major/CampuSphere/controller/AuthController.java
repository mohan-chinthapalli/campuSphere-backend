package com.major.CampuSphere.controller;

import com.major.CampuSphere.dto.request.LoginRequest;
import com.major.CampuSphere.dto.request.RegisterRequest;
import com.major.CampuSphere.dto.response.ApiResponse;
import com.major.CampuSphere.dto.response.AuthResponse;
import com.major.CampuSphere.dto.response.UserResponse;
import com.major.CampuSphere.security.CampuSpherePrincipal;
import com.major.CampuSphere.service.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login, register, current user")
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user")
    public ResponseEntity<ApiResponse<UserResponse>> me(
            @AuthenticationPrincipal CampuSpherePrincipal principal) {
        UserResponse response = authService.getCurrentUser(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout (client should discard the JWT)")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // JWT is stateless — logout is handled client-side by discarding the token.
        // A token blacklist can be added here in a future iteration if needed.
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
    }
}
