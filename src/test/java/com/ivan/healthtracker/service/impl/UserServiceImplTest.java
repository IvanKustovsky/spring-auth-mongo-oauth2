package com.ivan.healthtracker.service.impl;

import com.ivan.healthtracker.dto.LoginRequest;
import com.ivan.healthtracker.dto.RegisterRequest;
import com.ivan.healthtracker.exception.UserAlreadyExistsException;
import com.ivan.healthtracker.model.User;
import com.ivan.healthtracker.repository.UserRepository;
import com.ivan.healthtracker.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_shouldReturnJwtToken_whenUserDoesNotExist() {
        // Given
        var email = "user@email.com";
        var password = "pass123";
        var encodedPassword = "encoded_pass123";
        var jwtToken = "jwt_token_returned_value";

        var request = new RegisterRequest(email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken(any(User.class))).thenReturn(jwtToken);

        // When
        var result = userService.register(request);

        // Then
        assertThat(result.token()).isEqualTo(jwtToken);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenUserAlreadyExists() {
        // Given
        var request = new RegisterRequest("user@email.com", "pass123");
        var existingUser = new User();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Email already in use");
    }

    @Test
    void login_shouldReturnJwtToken_whenCredentialsAreCorrect() {
        // Given
        var email = "user@email.com";
        var password = "pass123";
        var token = "jwt_token_value";

        var user = User.builder()
                .id("123")
                .email(email)
                .password("encoded_pass")
                .roles(Set.of("USER"))
                .provider(User.AuthProvider.LOCAL)
                .build();

        var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtUtil.generateToken(user)).thenReturn(token);

        var request = new LoginRequest(email, password);

        // When
        var result = userService.login(request);

        // Then
        assertThat(result.token()).isEqualTo(token);
    }

    @Test
    void login_shouldThrow_whenAuthenticationFails() {
        // Given
        var request = new LoginRequest("user@email.com", "wrongPass");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        // When & Then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Bad credentials");

        verify(jwtUtil, never()).generateToken(any());
    }
}