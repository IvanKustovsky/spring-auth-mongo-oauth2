package com.ivan.healthtracker.service.impl;

import com.ivan.healthtracker.model.User;
import com.ivan.healthtracker.repository.UserRepository;
import com.ivan.healthtracker.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private OAuth2UserServiceImpl oAuth2UserService;

    @Test
    void handleOAuth2Login_shouldReturnToken_whenUserExists() {
        // Given
        var email = "existinguser@example.com";
        var name = "Existing User";
        var existingUser = User.builder()
                .email(email)
                .name(name)
                .provider(User.AuthProvider.GOOGLE)
                .roles(Collections.singleton("USER"))
                .build();
        var expectedToken = "jwt_token_existing_user";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(jwtUtil.generateToken(existingUser)).thenReturn(expectedToken);

        // When
        var response = oAuth2UserService.handleOAuth2Login(email, name);

        // Then
        assertThat(response.token()).isEqualTo(expectedToken);
        verify(userRepository, never()).save(any());
        verify(jwtUtil).generateToken(existingUser);
    }

    @Test
    void handleOAuth2Login_shouldCreateUserAndReturnToken_whenUserDoesNotExist() {
        // Given
        var email = "newuser@example.com";
        var name = "New User";
        var newUser = User.builder()
                .email(email)
                .name(name)
                .provider(User.AuthProvider.GOOGLE)
                .roles(Collections.singleton("USER"))
                .build();
        var expectedToken = "jwt_token_new_user";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken(any(User.class))).thenReturn(expectedToken);

        // When
        var response = oAuth2UserService.handleOAuth2Login(email, name);

        // Then
        assertThat(response.token()).isEqualTo(expectedToken);
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals(email) &&
                        user.getName().equals(name) &&
                        user.getProvider() == User.AuthProvider.GOOGLE &&
                        user.getRoles().contains("USER")
        ));
        verify(jwtUtil).generateToken(any(User.class));
    }
}