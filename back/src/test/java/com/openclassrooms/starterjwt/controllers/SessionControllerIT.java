package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    private Teacher teacher;
    private User user;

    @BeforeEach
    void setup() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();

        teacher = teacherRepository.save(Teacher.builder().firstName("Emma").lastName("Stone").build());
        user = userRepository.save(User.builder()
                .email("john@mail.com")
                .firstName("John")
                .lastName("Doe")
                .password("pwd")
                .admin(false)
                .build());
    }

    @Test
    @WithMockUser
    void findAll_shouldReturnSessions() throws Exception {
        Session session = Session.builder()
                .name("Morning Flow")
                .description("Breathing")
                .date(new Date())
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();
        sessionRepository.save(session);

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Morning Flow"));
    }

    @Test
    @WithMockUser
    void findById_shouldReturnSession() throws Exception {
        Session session = sessionRepository.save(Session.builder()
                .name("Focus Session")
                .description("Focus description")
                .date(new Date())
                .teacher(teacher)
                .users(new ArrayList<>())
                .build());

        mockMvc.perform(get("/api/session/{id}", session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.name").value("Focus Session"));
    }

    @Test
    @WithMockUser
    void findById_shouldReturnNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/session/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void findById_shouldReturnBadRequestWhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/session/{id}", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void create_shouldCreateSession() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Created Session");
        payload.put("description", "Description");
        payload.put("date", "2026-03-01T00:00:00.000Z");
        payload.put("teacher_id", teacher.getId());
        payload.put("users", new ArrayList<>());

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Created Session"));
    }

    @Test
    @WithMockUser
    void create_shouldReturnBadRequestWhenPayloadInvalid() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("description", "Description");

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void update_shouldUpdateSession() throws Exception {
        Session existing = sessionRepository.save(Session.builder()
                .name("Initial")
                .description("Initial desc")
                .date(new Date())
                .teacher(teacher)
                .users(new ArrayList<>())
                .build());

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Updated Session");
        payload.put("description", "Updated desc");
        payload.put("date", "2026-03-02T00:00:00.000Z");
        payload.put("teacher_id", teacher.getId());
        payload.put("users", new ArrayList<>());

        mockMvc.perform(put("/api/session/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Session"));
    }

    @Test
    @WithMockUser
    void participate_shouldAddUserToSession() throws Exception {
        Session existing = sessionRepository.save(Session.builder()
                .name("Morning Flow")
                .description("Breathing")
                .date(new Date())
                .teacher(teacher)
                .users(new ArrayList<>())
                .build());

        mockMvc.perform(post("/api/session/{id}/participate/{userId}", existing.getId(), user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void noLongerParticipate_shouldRemoveUserFromSession() throws Exception {
        Session existing = Session.builder()
                .name("Morning Flow")
                .description("Breathing")
                .date(new Date())
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();
        existing.getUsers().add(user);
        existing = sessionRepository.save(existing);

        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", existing.getId(), user.getId()))
                .andExpect(status().isOk());
    }
}
