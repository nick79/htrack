package com.nik.htrack.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "habit_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitSession {
    @Id
    @SequenceGenerator(name = "habit_session_sequence", sequenceName = "habit_session_sequence", allocationSize = 1)
    @GeneratedValue(generator = "habit_session_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotBlank
    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "start_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime start;

    @Column(name = "end_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "user_id",
                referencedColumnName = "id",
                nullable = false,
                foreignKey = @ForeignKey(name = "habit_Session_ht_user_id_fk"))
    private User user;
}
