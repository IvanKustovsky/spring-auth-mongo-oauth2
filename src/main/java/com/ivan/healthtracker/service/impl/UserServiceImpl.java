package com.ivan.healthtracker.service.impl;

import com.ivan.healthtracker.exception.ResourceNotFoundException;
import com.ivan.healthtracker.exception.UserAlreadyExistsException;
import com.ivan.healthtracker.model.User;
import com.ivan.healthtracker.dto.RegisterRequest;
import com.ivan.healthtracker.dto.LoginRequest;
import com.ivan.healthtracker.dto.AuthResponse;
import com.ivan.healthtracker.repositiry.UserRepository;
import com.ivan.healthtracker.utils.JwtUtil;
import com.ivan.healthtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .provider(User.AuthProvider.LOCAL)
                .roles(Collections.singleton("USER"))
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        authenticationManager.authenticate(authToken);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));

        String jwtToken = jwtUtil.generateToken(user);
        return new AuthResponse(jwtToken);
    }
} 