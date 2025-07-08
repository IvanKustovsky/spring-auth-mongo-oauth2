package com.ivan.healthtracker;

import com.ivan.healthtracker.config.AbstractMongoIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(SecurityConfigTestOverride.class)
@SpringBootTest
class HealthTrackerApiApplicationTests extends AbstractMongoIntegrationTest {

    @Test
    void contextLoads() {
    }

}
