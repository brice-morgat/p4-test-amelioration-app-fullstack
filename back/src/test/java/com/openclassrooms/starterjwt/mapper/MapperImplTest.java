package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MapperImplTest {

    @Test
    void userMapper_shouldMapEntityAndDtoAndLists() {
        UserMapperImpl mapper = new UserMapperImpl();
        LocalDateTime now = LocalDateTime.now();

        UserDto dto = new UserDto(1L, "john@mail.com", "Doe", "John", false, "pwd", now, now);
        User entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("john@mail.com", entity.getEmail());

        UserDto mappedDto = mapper.toDto(entity);
        assertNotNull(mappedDto);
        assertEquals("John", mappedDto.getFirstName());

        assertEquals(1, mapper.toEntity(List.of(dto)).size());
        assertEquals(1, mapper.toDto(List.of(entity)).size());

        assertNull(mapper.toEntity((UserDto) null));
        assertNull(mapper.toDto((User) null));
        assertNull(mapper.toEntity((List<UserDto>) null));
        assertNull(mapper.toDto((List<User>) null));
    }

    @Test
    void teacherMapper_shouldMapEntityAndDtoAndLists() {
        TeacherMapperImpl mapper = new TeacherMapperImpl();
        LocalDateTime now = LocalDateTime.now();

        TeacherDto dto = new TeacherDto(1L, "Doe", "Emma", now, now);
        Teacher entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("Emma", entity.getFirstName());

        TeacherDto mappedDto = mapper.toDto(entity);
        assertNotNull(mappedDto);
        assertEquals("Doe", mappedDto.getLastName());

        assertEquals(1, mapper.toEntity(List.of(dto)).size());
        assertEquals(1, mapper.toDto(List.of(entity)).size());

        assertNull(mapper.toEntity((TeacherDto) null));
        assertNull(mapper.toDto((Teacher) null));
        assertNull(mapper.toEntity((List<TeacherDto>) null));
        assertNull(mapper.toDto((List<Teacher>) null));
    }

    @Test
    void sessionMapper_shouldMapTeacherAndUsersBranches() {
        SessionMapperImpl mapper = new SessionMapperImpl();
        TeacherService teacherService = mock(TeacherService.class);
        UserService userService = mock(UserService.class);
        mapper.teacherService = teacherService;
        mapper.userService = userService;

        Teacher teacher = Teacher.builder().id(10L).firstName("Emma").lastName("Stone").build();
        User user1 = User.builder().id(1L).email("john@mail.com").firstName("John").lastName("Doe").password("pwd").admin(false).build();

        when(teacherService.findById(10L)).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(user1);
        when(userService.findById(2L)).thenReturn(null);

        SessionDto dto = new SessionDto();
        dto.setId(5L);
        dto.setName("Morning Flow");
        dto.setDescription("Breathing");
        dto.setDate(new Date());
        dto.setTeacher_id(10L);
        dto.setUsers(List.of(1L, 2L));

        Session entity = mapper.toEntity(dto);
        assertEquals(10L, entity.getTeacher().getId());
        assertEquals(2, entity.getUsers().size());
        assertEquals(1L, entity.getUsers().get(0).getId());
        assertNull(entity.getUsers().get(1));

        Session entityForDto = Session.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .date(entity.getDate())
                .teacher(entity.getTeacher())
                .users(List.of(user1))
                .build();

        SessionDto mappedDto = mapper.toDto(entityForDto);
        assertEquals(10L, mappedDto.getTeacher_id());
        assertEquals(1, mappedDto.getUsers().size());

        SessionDto dtoWithoutTeacherOrUsers = new SessionDto();
        dtoWithoutTeacherOrUsers.setName("No relations");
        dtoWithoutTeacherOrUsers.setDescription("Desc");
        dtoWithoutTeacherOrUsers.setDate(new Date());
        Session mappedWithoutTeacher = mapper.toEntity(dtoWithoutTeacherOrUsers);
        assertNull(mappedWithoutTeacher.getTeacher());
        assertEquals(0, mappedWithoutTeacher.getUsers().size());

        Session entityWithoutTeacherOrUsers = Session.builder()
                .id(6L)
                .name("No relations")
                .description("Desc")
                .date(new Date())
                .teacher(null)
                .users(null)
                .build();
        SessionDto mappedNoTeacher = mapper.toDto(entityWithoutTeacherOrUsers);
        assertNull(mappedNoTeacher.getTeacher_id());
        assertEquals(0, mappedNoTeacher.getUsers().size());

        assertEquals(1, mapper.toEntity(List.of(dto)).size());
        assertEquals(1, mapper.toDto(List.of(entityForDto)).size());

        assertNull(mapper.toEntity((SessionDto) null));
        assertNull(mapper.toDto((Session) null));
        assertNull(mapper.toEntity((List<SessionDto>) null));
        assertNull(mapper.toDto((List<Session>) null));
    }
}
