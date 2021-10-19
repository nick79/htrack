package com.nik.htrack.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordSessionCmd {
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
}
