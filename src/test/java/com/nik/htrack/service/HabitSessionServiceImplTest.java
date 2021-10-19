package com.nik.htrack.service;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.nik.htrack.domain.HabitSession;
import com.nik.htrack.domain.User;
import com.nik.htrack.exception.UserNotFoundException;
import com.nik.htrack.repository.HabitSessionRepository;
import com.nik.htrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class HabitSessionServiceImplTest {
    public static final String EMAIL = "first.last@example.com";
    public static final String HABIT_NAME = "Habit_1";

    @Mock
    private UserRepository userRepository;

    @Mock
    private HabitSessionRepository habitSessionRepository;

    private HabitSessionService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new HabitSessionServiceImpl(habitSessionRepository, userRepository);
    }

    @Test
    void shouldRecordHabitSessionForUser() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));

        // When
        underTest.recordSessionForUser(EMAIL, HABIT_NAME, LocalDateTime.now().minusMinutes(30L), LocalDateTime.now());

        // Then
        assertThat(user.getHabitSessions()).asList().hasSize(1);
    }

    @Test
    void shouldThrowWhenRecordingSessionForUserThatNotExists() {
        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.recordSessionForUser(EMAIL, HABIT_NAME, null, null))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining(format("User with email %s not found", EMAIL));
    }

    @Test
    void shouldReturnRecordedSessions() {
        // Given
        User user = new User(null, "First", "Last", EMAIL, "Password");
        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));

        List<HabitSession> sessions = Arrays.asList(new HabitSession(null,
                                                                     HABIT_NAME,
                                                                     LocalDateTime.now(),
                                                                     LocalDateTime.now(),
                                                                     user));
        given(habitSessionRepository.findByUser(user)).willReturn(sessions);

        // When
        // Then
        assertThat(underTest.getSessionsForUser(EMAIL)).asList().hasSize(1);
    }

    @Test
    void shouldThrowWhenReturningSessionsForUserThatNotExists() {
        // Given
        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getSessionsForUser(EMAIL))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining(format("User with email %s not found", EMAIL));
    }
}