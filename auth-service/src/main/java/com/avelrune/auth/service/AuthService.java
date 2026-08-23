package com.avelrune.auth.service;

import com.avelrune.auth.dto.AuthResponse;
import com.avelrune.auth.dto.LoginRequest;
import com.avelrune.auth.dto.RegisterRequest;
import com.avelrune.auth.dto.UserResponse;

import java.util.List;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    UserResponse me(String email);

    List<UserResponse> listUsers();
}
