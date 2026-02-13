package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void create_shouldSaveSession() {
        Session session = Session.builder().name("Morning Flow").build();
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.create(session);

        assertEquals("Morning Flow", result.getName());
    }

    @Test
    void delete_shouldDeleteWhenSessionExists() {
        Session session = Session.builder().id(1L).build();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        sessionService.delete(1L);

        verify(sessionRepository).deleteById(1L);
    }

    @Test
    void getById_shouldThrowWhenSessionMissing() {
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.getById(99L));
    }

    @Test
    void update_shouldSetIdAndSave() {
        Session existing = Session.builder().id(5L).build();
        Session update = Session.builder().name("Updated").build();

        when(sessionRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(sessionRepository.save(update)).thenReturn(update);

        Session result = sessionService.update(5L, update);

        assertEquals(5L, result.getId());
    }

    @Test
    void participate_shouldAddUserWhenNotAlreadyInSession() {
        User user = User.builder().id(1L).email("john@mail.com").lastName("Doe").firstName("John").password("x").admin(false).build();
        Session session = Session.builder().id(10L).users(new ArrayList<>()).build();

        when(sessionRepository.findById(10L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        sessionService.participate(10L, 1L);

        assertEquals(1, session.getUsers().size());
        verify(sessionRepository).save(session);
    }

    @Test
    void participate_shouldThrowWhenAlreadyParticipating() {
        User existing = User.builder().id(1L).email("john@mail.com").lastName("Doe").firstName("John").password("x").admin(false).build();
        Session session = Session.builder().id(10L).users(new ArrayList<>(List.of(existing))).build();

        when(sessionRepository.findById(10L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(BadRequestException.class, () -> sessionService.participate(10L, 1L));
    }

    @Test
    void participate_shouldThrowWhenUserMissing() {
        Session session = Session.builder().id(10L).users(new ArrayList<>()).build();

        when(sessionRepository.findById(10L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(10L, 1L));
    }

    @Test
    void noLongerParticipate_shouldRemoveUserWhenPresent() {
        User existing = User.builder().id(1L).email("john@mail.com").lastName("Doe").firstName("John").password("x").admin(false).build();
        Session session = Session.builder().id(10L).users(new ArrayList<>(List.of(existing))).build();

        when(sessionRepository.findById(10L)).thenReturn(Optional.of(session));

        sessionService.noLongerParticipate(10L, 1L);

        assertEquals(0, session.getUsers().size());
        verify(sessionRepository).save(session);
    }

    @Test
    void noLongerParticipate_shouldThrowWhenUserNotParticipating() {
        Session session = Session.builder().id(10L).users(new ArrayList<>()).build();
        when(sessionRepository.findById(10L)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(10L, 1L));
    }
}
