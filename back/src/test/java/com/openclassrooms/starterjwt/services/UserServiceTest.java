package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        com.openclassrooms.starterjwt.models.User appUser = com.openclassrooms.starterjwt.models.User.builder()
                .id(1L)
                .email("john@mail.com")
                .lastName("Doe")
                .firstName("John")
                .password("pwd")
                .admin(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(appUser));

        com.openclassrooms.starterjwt.models.User result = userService.findById(1L);

        assertEquals("john@mail.com", result.getEmail());
    }

    @Test
    void findById_shouldThrowWhenMissing() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(999L));
    }

    @Test
    void delete_shouldDeleteWhenAuthenticatedUserMatchesEmail() {
        com.openclassrooms.starterjwt.models.User appUser = com.openclassrooms.starterjwt.models.User.builder()
                .id(1L)
                .email("john@mail.com")
                .lastName("Doe")
                .firstName("John")
                .password("pwd")
                .admin(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(appUser));

        User principal = new User("john@mail.com", "pwd", java.util.List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowUnauthorizedWhenAuthenticatedEmailDoesNotMatch() {
        com.openclassrooms.starterjwt.models.User appUser = com.openclassrooms.starterjwt.models.User.builder()
                .id(1L)
                .email("john@mail.com")
                .lastName("Doe")
                .firstName("John")
                .password("pwd")
                .admin(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(appUser));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("other@mail.com", null)
        );

        assertThrows(UnauthorizedException.class, () -> userService.delete(1L));
    }

    @Test
    void delete_shouldThrowUnauthorizedWhenAuthenticationMissing() {
        com.openclassrooms.starterjwt.models.User appUser = com.openclassrooms.starterjwt.models.User.builder()
                .id(1L)
                .email("john@mail.com")
                .lastName("Doe")
                .firstName("John")
                .password("pwd")
                .admin(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(appUser));
        SecurityContextHolder.clearContext();

        assertThrows(UnauthorizedException.class, () -> userService.delete(1L));
    }
}
