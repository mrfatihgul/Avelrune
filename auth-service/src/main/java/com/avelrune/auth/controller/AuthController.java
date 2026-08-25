package com.avelrune.auth.controller;

import com.avelrune.auth.dto.AuthResponse;
import com.avelrune.auth.dto.LoginRequest;
import com.avelrune.auth.dto.RegisterRequest;
import com.avelrune.auth.dto.UserResponse;
import com.avelrune.auth.exception.ApiException;
import com.avelrune.auth.security.AuthHeaders;
import com.avelrune.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(
            @RequestHeader(value = "X-Authenticated-User", required = false) String email
    ) {
        if (email == null || email.isBlank()) {
            throw new ApiException(401, "Giriş yapmanız gerekiyor.");
        }
        return ResponseEntity.ok(authService.me(email));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> users(
            @RequestHeader(value = "X-Authenticated-Role", required = false) String role
    ) {
        AuthHeaders.requireAdmin(role);
        return ResponseEntity.ok(authService.listUsers());
    }
}
