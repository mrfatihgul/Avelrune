package com.avelrune.auth.service.impl;

import com.avelrune.auth.dto.AuthResponse;
import com.avelrune.auth.dto.LoginRequest;
import com.avelrune.auth.dto.RegisterRequest;
import com.avelrune.auth.dto.UserResponse;
import com.avelrune.auth.entity.AdminUser;
import com.avelrune.auth.exception.ApiException;
import com.avelrune.auth.repository.AdminUserRepository;
import com.avelrune.auth.security.JwtService;
import com.avelrune.auth.service.AuthService;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final AdminUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            AdminUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        AdminUser user = userRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> new ApiException(401, "E-posta veya şifre hatalı."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException(401, "E-posta veya şifre hatalı.");
        }
        return toAuth(user);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ApiException(409, "Bu e-posta ile kayıtlı bir hesap var.");
        }
        AdminUser user = new AdminUser();
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        return toAuth(userRepository.save(user));
    }

    @Override
    public UserResponse me(String email) {
        AdminUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(401, "Oturum geçersiz."));
        return toUser(user);
    }

    @Override
    public List<UserResponse> listUsers() {
        return userRepository.findAll(Sort.by("fullName"))
                .stream()
                .map(this::toUser)
                .toList();
    }

    private AuthResponse toAuth(AdminUser user) {
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs() / 1000,
                toUser(user)
        );
    }

    private UserResponse toUser(AdminUser user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }
}
