package com.nik.htrack.api;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.nik.htrack.domain.Role;
import com.nik.htrack.domain.User;
import com.nik.htrack.exception.BadRequestException;
import com.nik.htrack.exception.RoleNotFoundException;
import com.nik.htrack.exception.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-it.properties")
class UserControllerTest {

    private final Faker faker = new Faker();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {

    }

    @Test
    void shouldAddUser() throws Exception {
        // Given
        User user = createRandomUser();
        UserResponse expectedUserResponse = UserResponse.mapFromUser(user);
        mockMvc
            .perform(delete(UserController.BASE_API_PATH).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // When
        // Then
        mockMvc
            .perform(post(UserController.BASE_API_PATH)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(user))))
            .andExpect(status().isCreated());

        MvcResult getAllUsersResult = mockMvc
            .perform(get(UserController.BASE_API_PATH).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        List<UserResponse> users = objectMapper.readValue(getAllUsersResult.getResponse().getContentAsString(),
                                                          new TypeReference<>() {});
        assertThat(users).asList().hasSize(1);
        assertThat(users.get(0)).usingRecursiveComparison().isEqualTo(expectedUserResponse);

        MvcResult getUserResult = mockMvc
            .perform(get(UserController.BASE_API_PATH + "/" + user.getEmail()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        UserResponse userResponse = objectMapper.readValue(getUserResult.getResponse().getContentAsString(),
                                                           new TypeReference<>() {});
        assertThat(userResponse).usingRecursiveComparison().isEqualTo(expectedUserResponse);
    }

    @Test
    void shouldThrowIfUserNotFound() throws Exception {
        // Given
        User user = createRandomUser();

        // When
        mockMvc
            .perform(post(UserController.BASE_API_PATH)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(user))))
            .andExpect(status().isCreated());

        // Then
        MvcResult getUserResult = mockMvc
            .perform(get(UserController.BASE_API_PATH + "/INVALID_EMAIL").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
        assertThat(getUserResult.getResolvedException()).isInstanceOf(UserNotFoundException.class);

    }

    @Test
    void shouldDeleteUserWhenExists() throws Exception {
        // Given
        mockMvc
            .perform(delete(UserController.BASE_API_PATH).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        User user = createRandomUser();
        mockMvc
            .perform(post(UserController.BASE_API_PATH)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(user))))
            .andExpect(status().isCreated());

        // When
        mockMvc
            .perform(delete(UserController.BASE_API_PATH
                                + "/"
                                + user.getEmail()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Then
        MvcResult getUserResult = mockMvc
            .perform(get(UserController.BASE_API_PATH + "/" + user.getEmail()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
        assertThat(getUserResult.getResolvedException()).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldThrowWhenDeleteUserWhenNotExists() throws Exception {
        // Given
        mockMvc
            .perform(delete(UserController.BASE_API_PATH).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // When
        // Then
        MvcResult deleteUserResult = mockMvc
            .perform(delete(UserController.BASE_API_PATH + "/INVALID_EMAIL").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
        assertThat(deleteUserResult.getResolvedException()).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldAddRole() throws Exception {
        // Given
        Role role = new Role(null, "ROLE_NAME");
        RoleResponse expectedRoleResponse = RoleResponse.mapFromRole(role);
        mockMvc
            .perform(delete(UserController.BASE_API_PATH + "/roles").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // When
        // Then
        mockMvc
            .perform(post(UserController.BASE_API_PATH + "/roles")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(role))))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldThrowWhenAddRoleWhenExists() throws Exception {
        // Given
        Role role = new Role(null, "ROLE_NAME");
        RoleResponse expectedRoleResponse = RoleResponse.mapFromRole(role);
        mockMvc
            .perform(delete(UserController.BASE_API_PATH + "/roles").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        mockMvc
            .perform(post(UserController.BASE_API_PATH + "/roles")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(role))))
            .andExpect(status().isCreated());

        // When
        // Then
        MvcResult saveRoleResult = mockMvc
            .perform(post(UserController.BASE_API_PATH + "/roles")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(role))))
            .andExpect(status().isBadRequest())
            .andReturn();
        assertThat(saveRoleResult.getResolvedException()).isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldAddRoleToUser() throws Exception {
        // Given
        User user = createRandomUser();
        mockMvc
            .perform(delete(UserController.BASE_API_PATH).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        mockMvc
            .perform(post(UserController.BASE_API_PATH)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(user))))
            .andExpect(status().isCreated());

        Role role = new Role(null, "ROLE_NAME");
        mockMvc
            .perform(delete(UserController.BASE_API_PATH + "/roles").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        mockMvc
            .perform(post(UserController.BASE_API_PATH + "/roles")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(role))))
            .andExpect(status().isCreated());

        user.getRoles().add(role);
        UserResponse expectedUserResponse = UserResponse.mapFromUser(user);
        RoleToUserCmd roleToUserCmd = new RoleToUserCmd(user.getEmail(), "ROLE_NAME");

        // When
        mockMvc
            .perform(put(UserController.BASE_API_PATH)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(roleToUserCmd))))
            .andExpect(status().isOk());

        // Then
        MvcResult getAllUsersResult = mockMvc
            .perform(get(UserController.BASE_API_PATH).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        List<UserResponse> users = objectMapper.readValue(getAllUsersResult.getResponse().getContentAsString(),
                                                          new TypeReference<>() {});
        assertThat(users).asList().hasSize(1);
        assertThat(users.get(0)).usingRecursiveComparison().isEqualTo(expectedUserResponse);
    }

    @Test
    void shouldThrowWhenAddRoleToUserWhenUserNotExists() throws Exception {
        // Given
        mockMvc
            .perform(delete(UserController.BASE_API_PATH).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        RoleToUserCmd roleToUserCmd = new RoleToUserCmd("INVALID_EMAIL", "");

        // When
        // Then
        MvcResult addRoleToUserResult = mockMvc
            .perform(put(UserController.BASE_API_PATH)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(roleToUserCmd))))
            .andExpect(status().isNotFound())
            .andReturn();

        assertThat(addRoleToUserResult.getResolvedException()).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldThrowWhenAddRoleToUserWhenRoleNotExists() throws Exception {
        // Given
        User user = createRandomUser();
        mockMvc
            .perform(delete(UserController.BASE_API_PATH).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        mockMvc
            .perform(post(UserController.BASE_API_PATH)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(user))))
            .andExpect(status().isCreated());

        mockMvc
            .perform(delete(UserController.BASE_API_PATH + "/roles").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        RoleToUserCmd roleToUserCmd = new RoleToUserCmd(user.getEmail(), "INVALID_ROLE");

        // When
        // Then
        MvcResult addRoleToUserResult = mockMvc
            .perform(put(UserController.BASE_API_PATH)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(roleToUserCmd))))
            .andExpect(status().isNotFound())
            .andReturn();

        assertThat(addRoleToUserResult.getResolvedException()).isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    void shouldRecordSessionForUser() throws Exception {
        // Given
        User user = createRandomUser();
        mockMvc
            .perform(delete(UserController.BASE_API_PATH).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        mockMvc
            .perform(post(UserController.BASE_API_PATH)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(user))))
            .andExpect(status().isCreated());

        mockMvc
            .perform(delete(UserController.BASE_API_PATH
                                + "/"
                                + user.getEmail()
                                + "/habit").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        RecordSessionCmd recordSessionCmd = new RecordSessionCmd("Habit_1", LocalDateTime.now(), LocalDateTime.now());

        // When
        // Then
        MvcResult getSessionsResult = mockMvc
            .perform(get(UserController.BASE_API_PATH
                             + "/"
                             + user.getEmail()
                             + "/habit").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        List<RecordSessionResponse> sessions = objectMapper.readValue(getSessionsResult
                                                                          .getResponse()
                                                                          .getContentAsString(),
                                                                      new TypeReference<>() {});
        assertThat(sessions).asList().hasSize(0);

        mockMvc
            .perform(post(UserController.BASE_API_PATH + "/" + user.getEmail() + "/habit")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(recordSessionCmd))))
            .andExpect(status().isCreated());
        getSessionsResult = mockMvc
            .perform(get(UserController.BASE_API_PATH
                             + "/"
                             + user.getEmail()
                             + "/habit").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        sessions = objectMapper.readValue(getSessionsResult.getResponse().getContentAsString(),
                                          new TypeReference<>() {});
        assertThat(sessions).asList().hasSize(1);
    }

    @Test
    void shouldThrowWhenRecordSessionForUserThatNotExists() throws Exception {
        // Given
        User user = createRandomUser();
        mockMvc
            .perform(delete(UserController.BASE_API_PATH).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        mockMvc
            .perform(post(UserController.BASE_API_PATH)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(user))))
            .andExpect(status().isCreated());

        mockMvc
            .perform(delete(UserController.BASE_API_PATH
                                + "/"
                                + user.getEmail()
                                + "/habit").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        RecordSessionCmd recordSessionCmd = new RecordSessionCmd("Habit_1", LocalDateTime.now(), LocalDateTime.now());

        // When
        // Then
        MvcResult recordSessionForUser = mockMvc
            .perform(post(UserController.BASE_API_PATH + "/INVALID_EMAIL/habit")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(Objects.requireNonNull(objectInJson(recordSessionCmd))))
            .andExpect(status().isNotFound())
            .andReturn();
        assertThat(recordSessionForUser.getResolvedException()).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldThrowWhenDeleteSessionForUserThatNotExists() throws Exception {
        // When
        //Then
        MvcResult deleteSessionsForUserResult = mockMvc
            .perform(delete(UserController.BASE_API_PATH
                                + "/INVALID_EMAIL/habit").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
        assertThat(deleteSessionsForUserResult.getResolvedException()).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldThrowWhenGetSessionForUserThatNotExists() throws Exception {
        // When
        //Then
        MvcResult getSessionsForUserResult = mockMvc
            .perform(get(UserController.BASE_API_PATH + "/INVALID_EMAIL/habit").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
        assertThat(getSessionsForUserResult.getResolvedException()).isInstanceOf(UserNotFoundException.class);
    }

    private User createRandomUser() {
        User user = new User();
        user.setEmail(faker.internet().emailAddress());
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setPassword(faker.internet().password());
        return user;
    }

    private String objectInJson(Object objectToBeConverted) {
        try {
            return objectMapper.writeValueAsString(objectToBeConverted);
        } catch (JsonProcessingException e) {
            fail("Failed to convert user object to JSON");
            return null;
        }
    }
}