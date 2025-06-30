package com.ivan.healthtracker.service;

import com.ivan.healthtracker.dto.RegisterRequest;
import com.ivan.healthtracker.dto.LoginRequest;
import com.ivan.healthtracker.dto.AuthResponse;

public interface UserService {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout();
} 