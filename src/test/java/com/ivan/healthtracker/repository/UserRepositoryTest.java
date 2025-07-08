package com.ivan.healthtracker.repository;

import com.ivan.healthtracker.model.User;
import com.ivan.healthtracker.model.User.AuthProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataMongoTest
class UserRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnUser_whenUserExists() {
        // Given
        User user = User.builder()
                .email("ivan@email.com")
                .password("ivan123")
                .provider(AuthProvider.LOCAL)
                .roles(Set.of("USER"))
                .build();
        userRepository.save(user);

        // When
        Optional<User> result = userRepository.findByEmail("ivan@email.com");

        // Then
        assertThat(result).isPresent();
        assertThat("USER").isIn(result.get().getRoles());
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenUserDoesNotExist() {
        // Given
        var inputEmail = "user@email.com";

        // When
        Optional<User> result = userRepository.findByEmail(inputEmail);

        // Then
        assertThat(result).isEmpty();
    }
}