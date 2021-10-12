package com.nik.htrack.api;

import com.nik.htrack.domain.User;
import com.nik.htrack.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
}
