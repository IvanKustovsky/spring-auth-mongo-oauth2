package com.ivan.healthtracker.security;

import com.ivan.healthtracker.config.FrontendProperties;
import com.ivan.healthtracker.service.OAuth2UserService;
import com.ivan.healthtracker.dto.AuthResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2UserService oAuth2UserService;
    private final FrontendProperties frontendProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        AuthResponse authResponse = oAuth2UserService.handleOAuth2Login(email, name);

        String redirectUrl = frontendProperties.getRedirectUri() + "?token=" + authResponse.token();
        response.sendRedirect(redirectUrl);
    }
}