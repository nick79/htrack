package com.nik.htrack.service;

import static java.lang.String.format;

import com.nik.htrack.domain.HabitSession;
import com.nik.htrack.domain.User;
import com.nik.htrack.exception.UserNotFoundException;
import com.nik.htrack.repository.HabitSessionRepository;
import com.nik.htrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HabitSessionServiceImpl implements HabitSessionService {
    private final HabitSessionRepository habitSessionRepository;
    private final UserRepository userRepository;

    @Override
    public void recordSessionForUser(
        final String email, final String name, final LocalDateTime start, final LocalDateTime end) {
        log.debug("Recording habit session {} to user {}", name, email);
        User user = getUser(email);
        HabitSession habitSession = new HabitSession(null, name, start, end, user);
        user.getHabitSessions().add(habitSession);
    }

    @Override
    public List<HabitSession> getSessionsForUser(final String email) {
        log.debug("Fetching all recorded sessions for user {}", email);
        return habitSessionRepository.findByUser(getUser(email));
    }

    @Override
    public void deleteAllSessionsForUser(final String email) {
        log.debug("Deleting all sessions for user {}", email);
        User user = getUser(email);
        habitSessionRepository.deleteByUser(user);
    }

    private User getUser(final String email) {
        return userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(format("User with email %s not found", email)));
    }
}
