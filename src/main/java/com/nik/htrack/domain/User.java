package com.nik.htrack.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ht_user", uniqueConstraints = { @UniqueConstraint(name = "user_email_unique", columnNames = "email") })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(generator = "user_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false)
    private Long id;
    @NotBlank
    @Column(name = "first_name", nullable = false, columnDefinition = "TEXT")
    private String firstName;
    @NotBlank
    @Column(name = "last_name", nullable = false, columnDefinition = "TEXT")
    private String lastName;
    @Email
    @Column(name = "email", nullable = false, columnDefinition = "TEXT")
    private String email;
    @NotNull
    @Column(name = "password", nullable = false, columnDefinition = "TEXT")
    private String password;

    @ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
    @JoinTable(name = "ht_user_roles",
               joinColumns = @JoinColumn(name = "ht_user_id", foreignKey = @ForeignKey(name = "ht_user_id_fk")),
               inverseJoinColumns = @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "role_id_fk")))
    private final List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private final List<HabitSession> habitSessions = new ArrayList<>();
}
