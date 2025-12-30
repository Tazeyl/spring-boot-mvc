package sokolov.spring.springmvc.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sokolov.spring.springmvc.exception.ErrorMessageResponse;
import sokolov.spring.springmvc.model.UserDto;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSuccessCreateUser() throws Exception {

        var user = new UserDto();
        user.setName("test");
        user.setAge(18);
        user.setEmail("email@email.email");

        String userJson = objectMapper.writeValueAsString(user);

        String createdUserJson = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                )
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto createdUser = objectMapper.readValue(createdUserJson, UserDto.class);

        Assertions.assertNotNull(createdUser.getId());
        Assertions.assertEquals(user.getName(), createdUser.getName());


    }

    @Test
    void shouldErrorCreateUserWithValidException() throws Exception {

        var user = new UserDto();
        user.setName("   ");
        user.setAge(18);
        user.setEmail("email");

        String userJson = objectMapper.writeValueAsString(user);

        String errorJson = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                )
                .andExpect(status().is(400))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorMessageResponse errorMessageResponse = objectMapper.readValue(errorJson, ErrorMessageResponse.class);

        Assertions.assertEquals("Ошибка валидации запроса", errorMessageResponse.error());

    }

    @Test
    void shouldGetAllUsers() throws Exception {

        var user1 = new UserDto();
        user1.setName("test-1");
        user1.setAge(18);
        user1.setEmail("email-1@email.email");

        String userJson1 = objectMapper.writeValueAsString(user1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson1)
                )
                .andExpect(status().is(201));


        var user2 = new UserDto();
        user2.setName("test-2");
        user2.setAge(18);
        user2.setEmail("email-2@email.email");

        String userJson2 = objectMapper.writeValueAsString(user2);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson2)
                )
                .andExpect(status().is(201))
        ;


        String allUsersJson = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> allUsers = objectMapper.readValue(allUsersJson, new TypeReference<>() {
        });

        Assertions.assertEquals(2, allUsers.size());
        Assertions.assertTrue(allUsers.stream().anyMatch(userDto -> Objects.equals(userDto.getName(), user1.getName())));
        Assertions.assertTrue(allUsers.stream().anyMatch(userDto -> Objects.equals(userDto.getEmail(), user2.getEmail())));

    }

    @Test
    void shouldDeleteUser() throws Exception {

        createUser("test", "email@email.email", 18);
        createUser("test2", "email2@email.email", 19);

        mockMvc.perform(delete("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(204))
        ;

    }

    @Test
    void shouldDeleteUserNoSuchElementException() throws Exception {

        createUser("test", "email@email.email", 18);
        createUser("test2", "email2@email.email", 19);

        String errorJson = mockMvc.perform(delete("/users/4")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(404))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorMessageResponse errorMessageResponse = objectMapper.readValue(errorJson, ErrorMessageResponse.class);

        Assertions.assertEquals("Сущность не найдена", errorMessageResponse.error());

    }

    @Test
    void shouldGetUserById() throws Exception {

        createUser("test", "email@email.email", 18);
        String getUserJson = mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto getUser = objectMapper.readValue(getUserJson, UserDto.class);
        Assertions.assertNotNull(getUser.getId());
        Assertions.assertEquals("test", getUser.getName());
    }

    private String createUser(String name, String email, Integer age) throws Exception {
        var user = new UserDto();
        user.setName(name);
        user.setAge(age);
        user.setEmail(email);

        String userJson = objectMapper.writeValueAsString(user);

        return mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                )
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString();

    }
}