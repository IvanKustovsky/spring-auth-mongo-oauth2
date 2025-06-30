package com.ivan.healthtracker.service;

import com.ivan.healthtracker.dto.AuthResponse;

public interface OAuth2UserService {

    AuthResponse handleOAuth2Login(String email, String name);
}
