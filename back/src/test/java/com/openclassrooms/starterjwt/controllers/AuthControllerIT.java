package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void register_shouldCreateUser() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", "john@mail.com");
        payload.put("firstName", "John");
        payload.put("lastName", "Doe");
        payload.put("password", "secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void register_shouldReturnBadRequestWhenEmailAlreadyExists() throws Exception {
        userRepository.save(User.builder()
                .email("john@mail.com")
                .firstName("John")
                .lastName("Doe")
                .password("pwd")
                .admin(false)
                .build());

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", "john@mail.com");
        payload.put("firstName", "John");
        payload.put("lastName", "Doe");
        payload.put("password", "secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }

    @Test
    void login_shouldReturnJwtResponse() throws Exception {
        userRepository.save(User.builder()
                .email("john@mail.com")
                .firstName("John")
                .lastName("Doe")
                .password(passwordEncoder.encode("secret123"))
                .admin(true)
                .build());

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", "john@mail.com");
        payload.put("password", "secret123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("john@mail.com"))
                .andExpect(jsonPath("$.admin").value(true));
    }
}
