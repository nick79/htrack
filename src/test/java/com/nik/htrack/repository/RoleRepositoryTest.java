package com.nik.htrack.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.nik.htrack.domain.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class RoleRepositoryTest {

    public static final String ROLE_NAME = "ROLE_NAME";

    @Autowired
    private RoleRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindRoleByName() {
        // Given
        Role role = new Role(null, ROLE_NAME);
        underTest.save(role);

        // When
        Optional<Role> optionalRole = underTest.findByName(ROLE_NAME);

        // Then
        assertThat(optionalRole)
            .isPresent()
            .hasValueSatisfying(r -> assertThat(r).usingRecursiveComparison().ignoringFields("id").isEqualTo(role));
    }

    @Test
    void shouldNotFindRoleByNameWhenNameDoesNotExist() {
        // Given
        Role role = new Role(null, ROLE_NAME);
        underTest.save(role);

        // When
        Optional<Role> optionalRole = underTest.findByName("INVALID_ROLE_NAME");

        // Then
        assertThat(optionalRole).isNotPresent();
    }

    @Test
    void shouldCheckIfRoleNameExists() {
        // Given
        Role role = new Role(null, ROLE_NAME);
        underTest.save(role);

        // When
        Boolean exists = underTest.existsRoleByName(ROLE_NAME);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckIfRoleNameDoesNotExists() {
        // Given
        Role role = new Role(null, ROLE_NAME);
        underTest.save(role);

        // When
        Boolean exists = underTest.existsRoleByName("INVALID_ROLE_NAME");

        // Then
        assertThat(exists).isFalse();
    }
}