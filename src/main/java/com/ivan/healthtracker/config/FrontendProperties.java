package com.ivan.healthtracker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.frontend")
@Getter @Setter
public class FrontendProperties {

    private String redirectUri;
    private String origin;
}
