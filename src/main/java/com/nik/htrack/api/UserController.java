package com.nik.htrack.api;

import com.nik.htrack.domain.Role;
import com.nik.htrack.domain.User;
import com.nik.htrack.service.HabitSessionService;
import com.nik.htrack.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping(UserController.BASE_API_PATH)
@RequiredArgsConstructor
@Slf4j
public class UserController {
    public static final String BASE_API_PATH = "/api/v1/users";
    private final UserService userService;
    private final HabitSessionService habitSessionService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok().body(UserResponse.mapFromUsers(userService.getUsers()));
    }

    @GetMapping(path = "{email}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("email") String email) {
        return ResponseEntity.ok().body(UserResponse.mapFromUser(userService.getUser(email)));
    }

    @PostMapping
    public ResponseEntity<UserResponse> saveUser(@Valid @RequestBody User user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path(BASE_API_PATH).toUriString());
        return ResponseEntity.created(uri).body(UserResponse.mapFromUser(userService.saveUser(user)));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "{email}")
    public ResponseEntity<?> deleteUser(@PathVariable("email") String email) {
        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "roles")
    public ResponseEntity<RoleResponse> saveRole(@Valid @RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path(BASE_API_PATH).toUriString());
        return ResponseEntity.created(uri).body(RoleResponse.mapFromRole(userService.saveRole(role)));
    }

    @DeleteMapping(path = "roles")
    public ResponseEntity<?> deleteAllRoles() {
        userService.deleteAllRoles();
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserCmd roleToUserCmd) {
        userService.addRoleToUser(roleToUserCmd.getEmail(), roleToUserCmd.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "{email}/habit")
    public ResponseEntity<List<RecordSessionResponse>> getSessionsForUser(@PathVariable("email") String email) {
        return ResponseEntity
            .ok()
            .body(RecordSessionResponse.mapFromHabitSessions(habitSessionService.getSessionsForUser(email)));
    }

    @PostMapping(path = "{email}/habit")
    public ResponseEntity<?> recordSessionForUser(
        @PathVariable("email") String email, @RequestBody RecordSessionCmd recordSessionCmd) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path(BASE_API_PATH).toUriString());
        habitSessionService.recordSessionForUser(email,
                                                 recordSessionCmd.getName(),
                                                 recordSessionCmd.getStart(),
                                                 recordSessionCmd.getEnd());
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping(path = "{email}/habit")
    public ResponseEntity<?> deleteSessionsForUSer(@PathVariable("email") String email) {
        habitSessionService.deleteAllSessionsForUser(email);
        return ResponseEntity.noContent().build();
    }
}
