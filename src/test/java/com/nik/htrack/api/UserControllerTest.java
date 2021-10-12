package com.nik.htrack.api;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.nik.htrack.domain.User;
import com.nik.htrack.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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

    @Test
    void shouldAddUser() throws Exception {
        // Given
        User user = createRandomUser();
        UserResponse expectedUserResponse = UserResponse.mapFromUser(user);

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