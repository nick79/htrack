package com.nik.htrack.service;

import com.nik.htrack.domain.HabitSession;

import java.time.LocalDateTime;
import java.util.List;

public interface HabitSessionService {
    void recordSessionForUser(String email, String name, LocalDateTime start, LocalDateTime end);

    List<HabitSession> getSessionsForUser(String email);

    void deleteAllSessionsForUser(String email);
}
