package com.nik.htrack.repository;

import com.nik.htrack.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import javax.validation.constraints.Email;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(final @Email String email);

    boolean existsUserByEmail(final @Email String email);

    void deleteByEmail(final @Email String email);
}
