package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void findById_shouldReturnUser() throws Exception {
        User user = userRepository.save(User.builder()
                .email("john@mail.com")
                .firstName("John")
                .lastName("Doe")
                .password("pwd")
                .admin(false)
                .build());

        mockMvc.perform(get("/api/user/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@mail.com"));
    }

    @Test
    @WithMockUser
    void findById_shouldReturnNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/user/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void findById_shouldReturnBadRequestWhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/user/{id}", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "john@mail.com")
    void delete_shouldDeleteWhenAuthenticatedUserMatches() throws Exception {
        User user = userRepository.save(User.builder()
                .email("john@mail.com")
                .firstName("John")
                .lastName("Doe")
                .password("pwd")
                .admin(false)
                .build());

        mockMvc.perform(delete("/api/user/{id}", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "other@mail.com")
    void delete_shouldReturnUnauthorizedWhenAuthenticatedUserDiffers() throws Exception {
        User user = userRepository.save(User.builder()
                .email("john@mail.com")
                .firstName("John")
                .lastName("Doe")
                .password("pwd")
                .admin(false)
                .build());

        mockMvc.perform(delete("/api/user/{id}", user.getId()))
                .andExpect(status().isUnauthorized());
    }
}
