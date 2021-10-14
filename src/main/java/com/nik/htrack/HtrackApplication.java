package com.nik.htrack;

import com.nik.htrack.domain.Role;
import com.nik.htrack.domain.User;
import com.nik.htrack.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HtrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(HtrackApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(UserService userService) {
        return args -> {
            userService.saveRole(new Role(null, "ADMIN"));
            userService.saveRole(new Role(null, "USER"));

            userService.saveUser(new User(null, "Test", "Admin", "ta@example.com", "1234"));
            userService.saveUser(new User(null, "Test", "User", "tu@example.com", "1234"));

            userService.addRoleToUser("ta@example.com", "ADMIN");
            userService.addRoleToUser("ta@example.com", "USER");
            userService.addRoleToUser("tu@example.com", "USER");
        };
    }
}
