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
        Role role = new Role(1L, ROLE_NAME);
        underTest.save(role);

        // When
        Optional<Role> optionalRole = underTest.findByName(ROLE_NAME);

        // Then
        assertThat(optionalRole)
            .isPresent()
            .hasValueSatisfying(r -> assertThat(r).usingRecursiveComparison().isEqualTo(role));
    }

    @Test
    void shouldNotFindRoleByNameWhenNamelDoesNotExist() {
        // Given
        Role role = new Role(1L, ROLE_NAME);
        underTest.save(role);

        // When
        Optional<Role> optionalRole = underTest.findByName("INVALID_ROLE_NAME");

        // Then
        assertThat(optionalRole).isNotPresent();
    }
}