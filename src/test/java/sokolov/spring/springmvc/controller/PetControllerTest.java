package sokolov.spring.springmvc.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sokolov.spring.springmvc.exception.ErrorMessageResponse;
import sokolov.spring.springmvc.model.PetDto;
import sokolov.spring.springmvc.model.UserDto;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSuccessCreatePet() throws Exception {

        createUser("test", "email@email.email", 18);

        var pet = new PetDto();
        pet.setName("test");
        pet.setUserId(1L);

        String petJson = objectMapper.writeValueAsString(pet);

        String createdUserJson = mockMvc.perform(post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson)
                )
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PetDto createdPet = objectMapper.readValue(createdUserJson, PetDto.class);

        Assertions.assertNotNull(createdPet.getId());
        Assertions.assertEquals(pet.getName(), createdPet.getName());


    }

    @Test
    void shouldErrorCreatePetWithValidException() throws Exception {

        createUser("test", "email@email.email", 18);

        var pet = new PetDto();
        pet.setName("   ");
        pet.setUserId(1L);

        String petJson = objectMapper.writeValueAsString(pet);

        String errorJson = mockMvc.perform(post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson)
                )
                .andExpect(status().is(400))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorMessageResponse errorMessageResponse = objectMapper.readValue(errorJson, ErrorMessageResponse.class);

        Assertions.assertEquals("Ошибка валидации запроса", errorMessageResponse.error());

    }

    @Test
    void shouldErrorCreatePetWithNoSuchElementException() throws Exception {

        createUser("test", "email@email.email", 18);

        var pet = new PetDto();
        pet.setName("testPet");
        pet.setUserId(100L);

        String petJson = objectMapper.writeValueAsString(pet);

        String errorJson = mockMvc.perform(post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson)
                )
                .andExpect(status().is(404))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorMessageResponse errorMessageResponse = objectMapper.readValue(errorJson, ErrorMessageResponse.class);

        Assertions.assertEquals("Сущность не найдена", errorMessageResponse.error());

    }


    @Test
    void shouldGetAllPets() throws Exception {

        createUser("testUser", "email@email.email", 18);
        createPet("testPet1", 1L);
        createPet("testPet2", 1L);
        createUser("testUser2", "email2@email.email", 19);
        createPet("testPet3", 2L);
        createPet("testPet4", 2L);


        String allPetsJson = mockMvc.perform(get("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<PetDto> allPets = objectMapper.readValue(allPetsJson, new TypeReference<>() {
        });

        Assertions.assertEquals(4, allPets.size());
        Assertions.assertTrue(allPets.stream().anyMatch(petDto -> Objects.equals(petDto.getName(), "testPet1")));
        Assertions.assertTrue(allPets.stream().anyMatch(petDto -> Objects.equals(petDto.getUserId(), 2L)));

    }

    @Test
    void shouldDeletePets() throws Exception {

        createUser("testUser", "email@email.email", 18);
        createPet("testPet1", 1L);
        createPet("testPet2", 1L);

        String getUserJson = mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto getUser = objectMapper.readValue(getUserJson, UserDto.class);

        Assertions.assertEquals(2L, getUser.getPets().size());

        mockMvc.perform(delete("/pets/2")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(204));

        String getUserJson2 = mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto getUser2 = objectMapper.readValue(getUserJson2, UserDto.class);

        Assertions.assertEquals(1L, getUser2.getPets().size());

        String allPetsJson = mockMvc.perform(get("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<PetDto> allPets = objectMapper.readValue(allPetsJson, new TypeReference<>() {
        });

        Assertions.assertEquals(1, allPets.size());
    }

    @Test
    void shouldDeletePetNoSuchElementException() throws Exception {

        createUser("testUser", "email@email.email", 18);
        createPet("testPet1", 1L);
        createPet("testPet2", 1L);


        String errorJson = mockMvc.perform(delete("/pets/4")
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
    void shouldGetPetById() throws Exception {

        createUser("testUser", "email@email.email", 18);
        createPet("testPet1", 1L);

        String getPetJson = mockMvc.perform(get("/pets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PetDto getPet = objectMapper.readValue(getPetJson, PetDto.class);
        Assertions.assertNotNull(getPet.getId());
        Assertions.assertEquals("testPet1", getPet.getName());
        Assertions.assertEquals(1, getPet.getUserId());
    }


    // copyPaste UserControllerTest
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


    private String createPet(String name, Long userId) throws Exception {
        var pet = new PetDto();
        pet.setName(name);
        pet.setUserId(userId);

        String petJson = objectMapper.writeValueAsString(pet);

        return mockMvc.perform(post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson)
                )
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}