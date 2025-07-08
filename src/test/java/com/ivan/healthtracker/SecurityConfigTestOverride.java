package com.ivan.healthtracker;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@TestConfiguration
public class SecurityConfigTestOverride {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration fakeGoogle = CommonOAuth2Provider.GOOGLE
                .getBuilder("google")
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .build();

        return new InMemoryClientRegistrationRepository(fakeGoogle);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }
}
