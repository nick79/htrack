package com.nik.htrack.service;

import static java.lang.String.format;

import com.nik.htrack.domain.Role;
import com.nik.htrack.domain.User;
import com.nik.htrack.exception.BadRequestException;
import com.nik.htrack.exception.RoleNotFoundException;
import com.nik.htrack.exception.UserNotFoundException;
import com.nik.htrack.repository.RoleRepository;
import com.nik.htrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(final User user) {
        String email = user.getEmail();
        log.debug("Saving new user {}", email);
        boolean existsByEmail = userRepository.existsUserByEmail(email);
        if (existsByEmail) {
            throw new BadRequestException(format("User with email %s already exists", email));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(final Role role) {
        String name = role.getName();
        log.debug("Saving new role {}", name);
        boolean existsByName = roleRepository.existsRoleByName(name);
        if (existsByName) {
            throw new BadRequestException(format("Role with name %s already exists", name));
        }
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(final String email, final String roleName) {
        log.debug("Adding role {} to user {}", roleName, email);
        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(format("User with email %s not found", email)));
        Role role = roleRepository
            .findByName(roleName)
            .orElseThrow(() -> new RoleNotFoundException(format("Role with name %s not found", roleName)));
        user.getRoles().add(role);
    }

    @Override
    public User getUser(final String email) {
        log.debug("Fetching user {}", email);
        return userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(format("User with email %s not found", email)));
    }

    @Override
    public List<User> getUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll();
    }
}
