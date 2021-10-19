package com.nik.htrack.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.nik.htrack.domain.HabitSession;
import com.nik.htrack.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class HabitSessionRepositoryTest {

    public static final String HABIT_SESSION_NAME = "Habit_1";
    @Autowired
    private HabitSessionRepository underTest;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindByUser() {
        // Given
        User user1 = new User(null, "First", "Last", "first.last@example.com", "Password");
        user1 = userRepository.save(user1);
        User user2 = new User(null, "Name", "Last", "name.last@example.com", "Password");
        user2 = userRepository.save(user2);

        HabitSession habitSession = new HabitSession(null,
                                                     HABIT_SESSION_NAME,
                                                     LocalDateTime.now().minusMinutes(20L),
                                                     LocalDateTime.now(),
                                                     user1);
        underTest.save(habitSession);

        // When
        // Then
        assertThat(underTest.findByUserAndName(user1, HABIT_SESSION_NAME)).asList().hasSize(1);
        assertThat(underTest.findByUserAndName(user2, HABIT_SESSION_NAME)).asList().hasSize(0);
    }

    @Test
    void shouldFindByNameAndUser() {
        // Given
        User user = new User(null, "First", "Last", "first.last@example.com", "Password");
        user = userRepository.save(user);

        HabitSession habitSession = new HabitSession(null,
                                                     HABIT_SESSION_NAME,
                                                     LocalDateTime.now().minusMinutes(20L),
                                                     LocalDateTime.now(),
                                                     user);
        underTest.save(habitSession);

        // When
        // Then
        assertThat(underTest.findByUserAndName(user, HABIT_SESSION_NAME)).asList().hasSize(1);
        assertThat(underTest.findByUserAndName(user, "INVALID_HABIT_NAME")).asList().hasSize(0);

    }

    @Test
    void shouldReturnHabitSessionsByUserAndStartDateTime() {
        // Given
        User user = new User(null, "First", "Last", "first.last@example.com", "Password");
        user = userRepository.save(user);

        HabitSession habitSession1 = new HabitSession(null,
                                                      HABIT_SESSION_NAME,
                                                      LocalDateTime.now().minusMinutes(30L),
                                                      LocalDateTime.now().minusMinutes(20L),
                                                      user);
        underTest.save(habitSession1);

        HabitSession habitSession2 = new HabitSession(null,
                                                      HABIT_SESSION_NAME,
                                                      LocalDateTime.now().minusMinutes(15L),
                                                      LocalDateTime.now(),
                                                      user);
        underTest.save(habitSession2);

        // When
        // Then
        assertThat(underTest.findByUserAndStartIsAfter(user, LocalDateTime.now().minusMinutes(5))).asList().hasSize(0);
        assertThat(underTest.findByUserAndStartIsAfter(user, LocalDateTime.now().minusMinutes(16))).asList().hasSize(1);
        assertThat(underTest.findByUserAndStartIsAfter(user, LocalDateTime.now().minusMinutes(35))).asList().hasSize(2);
    }

    @Test
    void shouldReturnHabitSessionsByUserAndNameAndStartDateTime() {
        // Given
        User user = new User(null, "First", "Last", "first.last@example.com", "Password");
        user = userRepository.save(user);

        HabitSession habitSession1 = new HabitSession(null,
                                                      HABIT_SESSION_NAME,
                                                      LocalDateTime.now().minusMinutes(30L),
                                                      LocalDateTime.now().minusMinutes(20L),
                                                      user);
        underTest.save(habitSession1);

        HabitSession habitSession2 = new HabitSession(null,
                                                      "Habit_2",
                                                      LocalDateTime.now().minusMinutes(15L),
                                                      LocalDateTime.now(),
                                                      user);
        underTest.save(habitSession2);

        // When
        // Then
        assertThat(underTest.findByUserAndNameAndStartIsAfter(user,
                                                              HABIT_SESSION_NAME,
                                                              LocalDateTime.now().minusMinutes(31)))
            .asList()
            .hasSize(1);

        assertThat(underTest.findByUserAndNameAndStartIsAfter(user,
                                                              HABIT_SESSION_NAME,
                                                              LocalDateTime.now().minusMinutes(17)))
            .asList()
            .hasSize(0);

        assertThat(underTest.findByUserAndNameAndStartIsAfter(user, "Habit_2", LocalDateTime.now().minusMinutes(31)))
            .asList()
            .hasSize(1);
    }

    @Test
    void shouldReturnHabitSessionsByUserAndEndDateTime() {
        // Given
        User user = new User(null, "First", "Last", "first.last@example.com", "Password");
        user = userRepository.save(user);

        HabitSession habitSession1 = new HabitSession(null,
                                                      HABIT_SESSION_NAME,
                                                      LocalDateTime.now().minusMinutes(30L),
                                                      LocalDateTime.now().minusMinutes(20L),
                                                      user);
        underTest.save(habitSession1);

        HabitSession habitSession2 = new HabitSession(null,
                                                      HABIT_SESSION_NAME,
                                                      LocalDateTime.now().minusMinutes(15L),
                                                      LocalDateTime.now(),
                                                      user);
        underTest.save(habitSession2);

        // When
        // Then
        assertThat(underTest.findByUserAndEndIsBefore(user, LocalDateTime.now().minusMinutes(35))).asList().hasSize(0);
        assertThat(underTest.findByUserAndEndIsBefore(user, LocalDateTime.now().minusMinutes(16))).asList().hasSize(1);
        assertThat(underTest.findByUserAndEndIsBefore(user, LocalDateTime.now().plusMinutes(5))).asList().hasSize(2);
    }

    @Test
    void shouldReturnHabitSessionsByUserAndNameAndEndDateTime() {
        // Given
        User user = new User(null, "First", "Last", "first.last@example.com", "Password");
        user = userRepository.save(user);

        HabitSession habitSession1 = new HabitSession(null,
                                                      HABIT_SESSION_NAME,
                                                      LocalDateTime.now().minusMinutes(30L),
                                                      LocalDateTime.now().minusMinutes(20L),
                                                      user);
        underTest.save(habitSession1);

        HabitSession habitSession2 = new HabitSession(null,
                                                      "Habit_2",
                                                      LocalDateTime.now().minusMinutes(15L),
                                                      LocalDateTime.now(),
                                                      user);
        underTest.save(habitSession2);

        // When
        // Then
        assertThat(underTest.findByUserAndNameAndEndIsBefore(user,
                                                             HABIT_SESSION_NAME,
                                                             LocalDateTime.now().minusMinutes(31))).asList().hasSize(0);

        assertThat(underTest.findByUserAndNameAndEndIsBefore(user,
                                                             HABIT_SESSION_NAME,
                                                             LocalDateTime.now().minusMinutes(17))).asList().hasSize(1);

        assertThat(underTest.findByUserAndNameAndEndIsBefore(user, "Habit_2", LocalDateTime.now().plusMinutes(5)))
            .asList()
            .hasSize(1);
    }

    @Test
    void shouldReturnHabitSessionsByUserAndNameAndStartAndEndDateTime() {
        // Given
        User user = new User(null, "First", "Last", "first.last@example.com", "Password");
        user = userRepository.save(user);

        HabitSession habitSession1 = new HabitSession(null,
                                                      HABIT_SESSION_NAME,
                                                      LocalDateTime.now().minusMinutes(30L),
                                                      LocalDateTime.now().minusMinutes(20L),
                                                      user);
        underTest.save(habitSession1);

        HabitSession habitSession2 = new HabitSession(null,
                                                      "Habit_2",
                                                      LocalDateTime.now().minusMinutes(30L),
                                                      LocalDateTime.now().minusMinutes(20L),
                                                      user);
        underTest.save(habitSession2);

        // When
        // Then
        assertThat(underTest.findByUserAndNameAndStartIsAfterAndEndIsBefore(user,
                                                                            HABIT_SESSION_NAME,
                                                                            LocalDateTime.now().minusMinutes(35L),
                                                                            LocalDateTime.now().minusMinutes(23L)))
            .asList()
            .hasSize(0);

        assertThat(underTest.findByUserAndNameAndStartIsAfterAndEndIsBefore(user,
                                                                            HABIT_SESSION_NAME,
                                                                            LocalDateTime.now().minusMinutes(28L),
                                                                            LocalDateTime.now().minusMinutes(23L)))
            .asList()
            .hasSize(0);

        assertThat(underTest.findByUserAndNameAndStartIsAfterAndEndIsBefore(user,
                                                                            HABIT_SESSION_NAME,
                                                                            LocalDateTime.now().minusMinutes(31L),
                                                                            LocalDateTime.now().minusMinutes(19L)))
            .asList()
            .hasSize(1);
    }

    @Test
    void shouldDeleteAllRecordedSessionsForUser() {
        // Given
        User user = new User(null, "First", "Last", "first.last@example.com", "Password");
        user = userRepository.save(user);

        HabitSession habitSession1 = new HabitSession(null,
                                                      HABIT_SESSION_NAME,
                                                      LocalDateTime.now().minusMinutes(30L),
                                                      LocalDateTime.now().minusMinutes(20L),
                                                      user);
        underTest.save(habitSession1);
        HabitSession habitSession2 = new HabitSession(null,
                                                      "Habit_2",
                                                      LocalDateTime.now().minusMinutes(30L),
                                                      LocalDateTime.now().minusMinutes(20L),
                                                      user);
        underTest.save(habitSession2);

        // When
        underTest.deleteByUser(user);

        // Then
        assertThat(underTest.findByUser(user)).asList().hasSize(0);
    }
}