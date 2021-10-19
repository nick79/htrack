package com.nik.htrack.api;

import com.nik.htrack.domain.HabitSession;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class RecordSessionResponse {
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;

    public static RecordSessionResponse mapFromHabitSession(HabitSession habitSession) {
        RecordSessionResponse response = new RecordSessionResponse();
        response.setName(habitSession.getName());
        response.setStart(habitSession.getStart());
        response.setEnd(habitSession.getEnd());
        return response;
    }

    public static List<RecordSessionResponse> mapFromHabitSessions(List<HabitSession> habitSessions) {
        List<RecordSessionResponse> response = new ArrayList<>();
        for (HabitSession habitSession : habitSessions) {
            response.add(mapFromHabitSession(habitSession));
        }
        return response;
    }
}
