package com.avelrune.auth.service.impl;

import com.avelrune.auth.dto.AuthResponse;
import com.avelrune.auth.dto.LoginRequest;
import com.avelrune.auth.entity.AdminUser;
import com.avelrune.auth.exception.ApiException;
import com.avelrune.auth.repository.AdminUserRepository;
import com.avelrune.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AdminUserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    private AuthServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AuthServiceImpl(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void loginRejectsUnknownEmail() {
        LoginRequest request = new LoginRequest();
        request.setEmail("yok@avelrune.com");
        request.setPassword("x");
        when(userRepository.findByEmailIgnoreCase("yok@avelrune.com")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.login(request));
        assertEquals(401, ex.getStatus());
    }

    @Test
    void loginRejectsWrongPassword() {
        AdminUser user = user(2L, "user@avelrune.com", "USER");
        when(userRepository.findByEmailIgnoreCase("user@avelrune.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("yanlis", "hash")).thenReturn(false);

        LoginRequest request = new LoginRequest();
        request.setEmail("user@avelrune.com");
        request.setPassword("yanlis");

        ApiException ex = assertThrows(ApiException.class, () -> service.login(request));
        assertEquals(401, ex.getStatus());
    }

    @Test
    void loginReturnsBearerToken() {
        AdminUser user = user(2L, "user@avelrune.com", "USER");
        when(userRepository.findByEmailIgnoreCase("user@avelrune.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("ok", "hash")).thenReturn(true);
        when(jwtService.generateToken(2L, "user@avelrune.com", "USER")).thenReturn("tok");
        when(jwtService.getExpirationMs()).thenReturn(8000L);

        LoginRequest request = new LoginRequest();
        request.setEmail("user@avelrune.com");
        request.setPassword("ok");

        AuthResponse response = service.login(request);
        assertEquals("tok", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(2L, response.getUser().getId());
        assertEquals("USER", response.getUser().getRole());
    }

    private static AdminUser user(Long id, String email, String role) {
        AdminUser user = new AdminUser();
        user.setId(id);
        user.setEmail(email);
        user.setFullName("Test");
        user.setRole(role);
        user.setPasswordHash("hash");
        return user;
    }
}
