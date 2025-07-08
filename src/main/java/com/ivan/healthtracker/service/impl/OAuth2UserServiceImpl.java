package com.ivan.healthtracker.service.impl;

import com.ivan.healthtracker.dto.AuthResponse;
import com.ivan.healthtracker.model.User;
import com.ivan.healthtracker.repository.UserRepository;
import com.ivan.healthtracker.service.OAuth2UserService;
import com.ivan.healthtracker.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse handleOAuth2Login(String email, String name) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .name(name)
                            .provider(User.AuthProvider.GOOGLE)
                            .roles(Collections.singleton("USER"))
                            .build();
                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token);
    }
}
