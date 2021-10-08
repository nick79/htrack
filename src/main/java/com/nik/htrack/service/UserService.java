package com.nik.htrack.service;

import com.nik.htrack.domain.Role;
import com.nik.htrack.domain.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    Role saveRole(Role role);

    void addRoleToUser(String email, String roleName);

    User getUser(String email);

    List<User> getUsers();
}
