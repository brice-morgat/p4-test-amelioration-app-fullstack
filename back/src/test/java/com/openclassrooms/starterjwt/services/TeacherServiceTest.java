package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @Test
    void findAll_shouldReturnTeachers() {
        Teacher teacher = Teacher.builder().id(1L).firstName("Emma").lastName("Stone").build();
        when(teacherRepository.findAll()).thenReturn(List.of(teacher));

        List<Teacher> result = teacherService.findAll();

        assertEquals(1, result.size());
        assertEquals("Emma", result.getFirst().getFirstName());
    }

    @Test
    void findById_shouldReturnTeacherWhenExists() {
        Teacher teacher = Teacher.builder().id(1L).firstName("Emma").lastName("Stone").build();
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.findById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void findById_shouldThrowNotFoundWhenMissing() {
        when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> teacherService.findById(999L));
    }
}
