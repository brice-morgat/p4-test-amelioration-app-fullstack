package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TeacherControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setup() {
        teacherRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void findAll_shouldReturnTeachers() throws Exception {
        teacherRepository.save(Teacher.builder().firstName("Emma").lastName("Stone").build());

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Emma"));
    }

    @Test
    @WithMockUser
    void findById_shouldReturnTeacher() throws Exception {
        Teacher teacher = teacherRepository.save(Teacher.builder().firstName("Emma").lastName("Stone").build());

        mockMvc.perform(get("/api/teacher/{id}", teacher.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teacher.getId()));
    }

    @Test
    @WithMockUser
    void findById_shouldReturnNotFoundWhenMissing() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void findById_shouldReturnBadRequestWhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", "abc"))
                .andExpect(status().isBadRequest());
    }
}
