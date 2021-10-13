package com.nik.htrack.api;

import com.nik.htrack.domain.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class RoleResponse {
    private String name;

    public static RoleResponse mapFromRole(final Role role) {
        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setName(role.getName());
        return roleResponse;
    }

    public static List<RoleResponse> mapFromRoles(final List<Role> roles) {
        List<RoleResponse> roleResponses = new ArrayList<>();
        for (Role r : roles) {
            roleResponses.add(mapFromRole(r));
        }
        return roleResponses;
    }
}
