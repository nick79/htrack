package com.nik.htrack.repository;

import com.nik.htrack.domain.HabitSession;
import com.nik.htrack.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotBlank;

public interface HabitSessionRepository extends JpaRepository<HabitSession, Long> {
    List<HabitSession> findByUser(final User user);

    List<HabitSession> findByUserAndName(final User user, final @NotBlank String name);

    List<HabitSession> findByUserAndStartIsAfter(final User user, final LocalDateTime start);

    List<HabitSession> findByUserAndNameAndStartIsAfter(
        final User user, final @NotBlank String name, final LocalDateTime start);

    List<HabitSession> findByUserAndEndIsBefore(final User user, final LocalDateTime end);

    List<HabitSession> findByUserAndNameAndEndIsBefore(
        final User user, final @NotBlank String name, final LocalDateTime end);

    List<HabitSession> findByUserAndNameAndStartIsAfterAndEndIsBefore(
        final User user, final @NotBlank String name, final LocalDateTime start, final LocalDateTime end);
}
