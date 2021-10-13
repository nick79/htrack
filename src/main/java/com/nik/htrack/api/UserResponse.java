package com.nik.htrack.api;

import com.nik.htrack.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UserResponse {
    private String firstName;
    private String lastName;
    private String email;
    private List<RoleResponse> roles = new ArrayList<>();

    public static UserResponse mapFromUser(final User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setRoles(RoleResponse.mapFromRoles(user.getRoles()));
        return userResponse;
    }

    public static List<UserResponse> mapFromUsers(final List<User> users) {
        List<UserResponse> userResponses = new ArrayList<>();
        for (User u : users) {
            userResponses.add(mapFromUser(u));
        }
        return userResponses;
    }
}
