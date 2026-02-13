package com.openclassrooms.starterjwt.contracts;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PojoContractsTest {

    @Test
    void equalsAndHashCode_shouldWorkForLoginRequest() {
        LoginRequest a = new LoginRequest();
        a.setEmail("john@mail.com");
        a.setPassword("secret123");

        LoginRequest b = new LoginRequest();
        b.setEmail("john@mail.com");
        b.setPassword("secret123");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setEmail("other@mail.com");
        assertNotEquals(a, b);

        b.setEmail("john@mail.com");
        b.setPassword("different");
        assertNotEquals(a, b);

        assertFalse(a.equals(null));
        assertFalse(a.equals("x"));
    }

    @Test
    void equalsAndHashCode_shouldWorkForSignupRequest() {
        SignupRequest a = new SignupRequest();
        a.setEmail("john@mail.com");
        a.setFirstName("John");
        a.setLastName("Doe");
        a.setPassword("secret123");

        SignupRequest b = new SignupRequest();
        b.setEmail("john@mail.com");
        b.setFirstName("John");
        b.setLastName("Doe");
        b.setPassword("secret123");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setEmail("other@mail.com");
        assertNotEquals(a, b);
        b.setEmail("john@mail.com");

        b.setFirstName("Jane");
        assertNotEquals(a, b);
        b.setFirstName("John");

        b.setLastName("Smith");
        assertNotEquals(a, b);
        b.setLastName("Doe");

        b.setPassword("other");
        assertNotEquals(a, b);
    }

    @Test
    void equalsAndHashCode_shouldWorkForMessageResponse() {
        MessageResponse a = new MessageResponse("ok");
        MessageResponse b = new MessageResponse("ok");
        MessageResponse c = new MessageResponse("ko");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertTrue(a.toString().contains("ok"));
    }

    @Test
    void equalsAndHashCode_shouldWorkForUserDto() {
        LocalDateTime now = LocalDateTime.now();
        UserDto a = new UserDto(1L, "john@mail.com", "Doe", "John", false, "pwd", now, now);
        UserDto b = new UserDto(1L, "john@mail.com", "Doe", "John", false, "pwd", now, now);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setId(2L);
        assertNotEquals(a, b);
        b.setId(1L);

        b.setEmail("other@mail.com");
        assertNotEquals(a, b);
        b.setEmail("john@mail.com");

        b.setLastName("Smith");
        assertNotEquals(a, b);
        b.setLastName("Doe");

        b.setFirstName("Jane");
        assertNotEquals(a, b);
        b.setFirstName("John");

        b.setAdmin(true);
        assertNotEquals(a, b);
        b.setAdmin(false);

        b.setPassword("new");
        assertNotEquals(a, b);
        b.setPassword("pwd");

        b.setCreatedAt(now.minusDays(1));
        assertNotEquals(a, b);
        b.setCreatedAt(now);

        b.setUpdatedAt(now.minusHours(1));
        assertNotEquals(a, b);
    }

    @Test
    void equalsAndHashCode_shouldWorkForTeacherDto() {
        LocalDateTime now = LocalDateTime.now();
        TeacherDto a = new TeacherDto(1L, "Doe", "Emma", now, now);
        TeacherDto b = new TeacherDto(1L, "Doe", "Emma", now, now);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setId(2L);
        assertNotEquals(a, b);
        b.setId(1L);

        b.setLastName("Stone");
        assertNotEquals(a, b);
        b.setLastName("Doe");

        b.setFirstName("Anna");
        assertNotEquals(a, b);
        b.setFirstName("Emma");

        b.setCreatedAt(now.minusDays(1));
        assertNotEquals(a, b);
        b.setCreatedAt(now);

        b.setUpdatedAt(now.minusHours(1));
        assertNotEquals(a, b);
    }

    @Test
    void equalsAndHashCode_shouldWorkForSessionDto() {
        Date now = new Date();
        LocalDateTime dt = LocalDateTime.now();
        SessionDto a = new SessionDto(1L, "Flow", now, 5L, "Desc", List.of(1L, 2L), dt, dt);
        SessionDto b = new SessionDto(1L, "Flow", now, 5L, "Desc", List.of(1L, 2L), dt, dt);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setId(2L);
        assertNotEquals(a, b);
        b.setId(1L);

        b.setName("Other");
        assertNotEquals(a, b);
        b.setName("Flow");

        b.setDate(new Date(now.getTime() + 1000));
        assertNotEquals(a, b);
        b.setDate(now);

        b.setTeacher_id(6L);
        assertNotEquals(a, b);
        b.setTeacher_id(5L);

        b.setDescription("Other");
        assertNotEquals(a, b);
        b.setDescription("Desc");

        b.setUsers(List.of(3L));
        assertNotEquals(a, b);
        b.setUsers(List.of(1L, 2L));

        b.setCreatedAt(dt.minusDays(1));
        assertNotEquals(a, b);
        b.setCreatedAt(dt);

        b.setUpdatedAt(dt.minusHours(1));
        assertNotEquals(a, b);
    }

    @Test
    void equals_shouldCompareEntitiesById() {
        User user1 = User.builder().id(1L).email("john@mail.com").lastName("Doe").firstName("John").password("pwd").admin(false).build();
        User user2 = User.builder().id(1L).email("other@mail.com").lastName("X").firstName("Y").password("pwd2").admin(true).build();
        User user3 = User.builder().id(2L).email("john@mail.com").lastName("Doe").firstName("John").password("pwd").admin(false).build();

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);

        Teacher teacher1 = Teacher.builder().id(1L).firstName("Emma").lastName("Stone").build();
        Teacher teacher2 = Teacher.builder().id(1L).firstName("E").lastName("S").build();
        Teacher teacher3 = Teacher.builder().id(2L).firstName("Emma").lastName("Stone").build();

        assertEquals(teacher1, teacher2);
        assertNotEquals(teacher1, teacher3);

        Session session1 = Session.builder().id(1L).name("Flow").description("Desc").date(new Date()).teacher(teacher1).users(List.of(user1)).build();
        Session session2 = Session.builder().id(1L).name("Other").description("Other").date(new Date()).teacher(teacher2).users(List.of(user2)).build();
        Session session3 = Session.builder().id(2L).name("Flow").description("Desc").date(new Date()).teacher(teacher1).users(List.of(user1)).build();

        assertEquals(session1, session2);
        assertNotEquals(session1, session3);
    }
}
