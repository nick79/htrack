package com.nik.htrack.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.nik.htrack.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserRepositoryTest {

    public static final String EMAIL = "first.last@example.com";

    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        underTest.save(user);

        // When
        Optional<User> optionalUser = underTest.findByEmail(EMAIL);

        // Then
        assertThat(optionalUser)
            .isPresent()
            .hasValueSatisfying(u -> assertThat(u).usingRecursiveComparison().ignoringFields("id").isEqualTo(user));
    }

    @Test
    void shouldNotFindUserByEmailWhenEmailDoesNotExist() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        underTest.save(user);

        // When
        Optional<User> optionalUser = underTest.findByEmail("email@example.com");

        // Then
        assertThat(optionalUser).isNotPresent();
    }

    @Test
    void shouldCheckIfUserEmailExists() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        underTest.save(user);

        // When
        Boolean exists = underTest.existsUserByEmail(EMAIL);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckIfUserEmailDoesNotExists() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        underTest.save(user);

        // When
        Boolean exists = underTest.existsUserByEmail("email@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void shouldDeleteUserWhenExists() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        underTest.save(user);

        // When
        underTest.deleteByEmail(EMAIL);
        Boolean exists = underTest.existsUserByEmail(EMAIL);

        // Then
        assertThat(exists).isFalse();
    }
}