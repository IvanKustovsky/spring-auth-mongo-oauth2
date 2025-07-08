package com.ivan.healthtracker.controller;

import com.ivan.healthtracker.SecurityConfigTestOverride;
import com.ivan.healthtracker.config.AbstractMongoIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Import(SecurityConfigTestOverride.class)
@SpringBootTest
class AuthControllerTest extends AbstractMongoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String REGISTER_ENDPOINT = "/api/v1/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";

    private static final String DEFAULT_EMAIL = "ivan@example.com";
    private static final String DEFAULT_PASSWORD = "SecurePass123";

    private String toJson(String email, String password) {
        return """
            {
              "email": "%s",
              "password": "%s"
            }
            """.formatted(email, password);
    }

    @Test
    void register_shouldReturnToken_whenUserDoesNotExist() throws Exception {
        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(DEFAULT_EMAIL, DEFAULT_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void register_shouldReturnException_whenUserAlreadyExist() throws Exception {
        // Register first time
        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(DEFAULT_EMAIL, DEFAULT_PASSWORD)))
                .andExpect(status().isOk());

        // Register second time
        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(DEFAULT_EMAIL, DEFAULT_PASSWORD)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreCorrect() throws Exception {
        // Register user first
        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(DEFAULT_EMAIL, DEFAULT_PASSWORD)))
                .andExpect(status().isOk());

        // Then login
        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(DEFAULT_EMAIL, DEFAULT_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_shouldReturnException_whenCredentialsAreIncorrect() throws Exception {
        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson("wrongemail@example.com", "wrongPass123")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }
}