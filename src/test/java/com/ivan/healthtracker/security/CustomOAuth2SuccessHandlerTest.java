package com.ivan.healthtracker.security;

import com.ivan.healthtracker.config.FrontendProperties;
import com.ivan.healthtracker.dto.AuthResponse;
import com.ivan.healthtracker.service.OAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2SuccessHandlerTest {

    @Mock
    private OAuth2UserService oAuth2UserService;

    @Mock
    private FrontendProperties frontendProperties;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2User oAuth2User;

    @InjectMocks
    private CustomOAuth2SuccessHandler successHandler;

    @Test
    void onAuthenticationSuccess_shouldRedirectWithToken() throws Exception {
        // Given
        String email = "user@example.com";
        String name = "Ivan";
        String token = "mocked_jwt_token";
        String frontendRedirectUri = "http://localhost:3000/oauth2/success";

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn(email);
        when(oAuth2User.getAttribute("name")).thenReturn(name);
        when(frontendProperties.getRedirectUri()).thenReturn(frontendRedirectUri);
        when(oAuth2UserService.handleOAuth2Login(email, name))
                .thenReturn(new AuthResponse(token));

        // Mock HttpServletResponse
        doNothing().when(response).sendRedirect(anyString());

        // When
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        String expectedRedirect = frontendRedirectUri + "?token=" + token;
        verify(response).sendRedirect(expectedRedirect);
        verify(oAuth2UserService).handleOAuth2Login(email, name);
    }
}
