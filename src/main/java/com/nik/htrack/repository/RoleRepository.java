package com.nik.htrack.repository;

import com.nik.htrack.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import javax.validation.constraints.NotBlank;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(final @NotBlank String name);

    boolean existsRoleByName(final @NotBlank String name);
}
