package com.nik.htrack.service;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.nik.htrack.domain.Role;
import com.nik.htrack.domain.User;
import com.nik.htrack.exception.BadRequestException;
import com.nik.htrack.exception.RoleNotFoundException;
import com.nik.htrack.exception.UserNotFoundException;
import com.nik.htrack.repository.RoleRepository;
import com.nik.htrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

class UserServiceImplTest {
    public static final String EMAIL = "first.last@example.com";
    public static final String ROLE_NAME = "ROLE_NAME";

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<Role> roleArgumentCaptor;

    private UserService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new UserServiceImpl(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void shouldSaveNewUser() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        given(userRepository.existsUserByEmail(EMAIL)).willReturn(false);

        // When
        underTest.saveUser(user);

        // Then
        then(userRepository).should().save(userArgumentCaptor.capture());
        User userArgumentCaptorValue = userArgumentCaptor.getValue();
        assertThat(userArgumentCaptorValue).usingRecursiveComparison().ignoringFields("id").isEqualTo(user);
    }

    @Test
    void shouldThrownWhenUserWithEmailAlreadyExists() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        given(userRepository.existsUserByEmail(EMAIL)).willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> underTest.saveUser(user))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining(format("User with email %s already exists", EMAIL));
        then(userRepository).should(never()).save(any());
    }

    @Test
    void shouldSaveNewRole() {
        // Given
        Role role = new Role(null, ROLE_NAME);
        given(roleRepository.existsRoleByName(ROLE_NAME)).willReturn(false);

        // When
        underTest.saveRole(role);

        // Then
        then(roleRepository).should().save(roleArgumentCaptor.capture());
        Role roleArgumentCaptorValue = roleArgumentCaptor.getValue();
        assertThat(roleArgumentCaptorValue).usingRecursiveComparison().ignoringFields("id").isEqualTo(role);
    }

    @Test
    void shouldThrownWhenRoleWithNameAlreadyExists() {
        // Given
        Role role = new Role(null, ROLE_NAME);
        given(roleRepository.existsRoleByName(ROLE_NAME)).willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> underTest.saveRole(role))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining(format("Role with name %s already exists", ROLE_NAME));
        then(userRepository).should(never()).save(any());
    }

    @Test
    void shouldAddRoleToUser() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));

        Role role = new Role(null, ROLE_NAME);
        given(roleRepository.findByName(ROLE_NAME)).willReturn(Optional.of(role));

        // When
        underTest.addRoleToUser(EMAIL, ROLE_NAME);

        // Then
        assertThat(user.getRoles()).asList().contains(role);
    }

    @Test
    void shouldThrowWhenAddingRoleIfUserNotExists() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.addRoleToUser(EMAIL, ROLE_NAME))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining(format("User with email %s not found", EMAIL));
    }

    @Test
    void shouldThrowWhenAddingRoleIfRoleNotExists() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));

        Role role = new Role(null, ROLE_NAME);
        given(roleRepository.findByName(ROLE_NAME)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.addRoleToUser(EMAIL, ROLE_NAME))
            .isInstanceOf(RoleNotFoundException.class)
            .hasMessageContaining(format("Role with name %s not found", ROLE_NAME));
    }

    @Test
    void shouldThrowWhenUserNotExists() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getUser(EMAIL))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining(format("User with email %s not found", EMAIL));
    }

    @Test
    void shouldDeleteUserWhenExists() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));

        // When
        underTest.deleteUser(EMAIL);

        // Then
        then(userRepository).should().findByEmail(EMAIL);
        then(userRepository).should().deleteByEmail(EMAIL);
    }

    @Test
    void shouldThrowWhenDeleteUserWhenNotExists() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.deleteUser(EMAIL))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining(format("User with email %s not found", EMAIL));
    }

    @Test
    void shouldDeleteRoleWhenExists() {
        // Given
        Role role = new Role(null, ROLE_NAME);
        given(roleRepository.findByName(ROLE_NAME)).willReturn(Optional.of(role));

        // When
        underTest.deleteRole(ROLE_NAME);

        // Then
        then(roleRepository).should().findByName(ROLE_NAME);
        then(roleRepository).should().deleteByName(ROLE_NAME);
    }

    @Test
    void shouldThrowWhenDeleteRoleWhenNotExists() {
        // Given
        Role role = new Role(null, ROLE_NAME);
        given(roleRepository.findByName(ROLE_NAME)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.deleteRole(ROLE_NAME))
            .isInstanceOf(RoleNotFoundException.class)
            .hasMessageContaining(format("Role with name %s not found", ROLE_NAME));
    }
}