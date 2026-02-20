package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.EmailAlreadyUsedException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @Test
    void isAdmin_shouldReturnTrueWhenUserIsAdmin() {
        User user = User.builder()
                .email("admin@mail.com")
                .lastName("Admin")
                .firstName("Super")
                .password("encoded-password")
                .admin(true)
                .build();
        when(userRepository.findByEmail("admin@mail.com")).thenReturn(Optional.of(user));

        boolean result = authService.isAdmin("admin@mail.com");

        assertTrue(result);
    }

    @Test
    void isAdmin_shouldReturnFalseWhenUserMissing() {
        when(userRepository.findByEmail("missing@mail.com")).thenReturn(Optional.empty());

        boolean result = authService.isAdmin("missing@mail.com");

        assertFalse(result);
    }

    @Test
    void register_shouldSaveUserWhenEmailIsAvailable() {
        SignupRequest request = new SignupRequest();
        request.setEmail("john@mail.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("secret123");

        when(userRepository.existsByEmail("john@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User saved = userCaptor.getValue();
        assertEquals("john@mail.com", saved.getEmail());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertEquals("encoded-password", saved.getPassword());
        assertFalse(saved.isAdmin());
    }

    @Test
    void register_shouldThrowWhenEmailAlreadyUsed() {
        SignupRequest request = new SignupRequest();
        request.setEmail("john@mail.com");

        when(userRepository.existsByEmail("john@mail.com")).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> authService.register(request));
    }

    @Test
    void authenticate_shouldReturnJwtResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@mail.com");
        request.setPassword("secret123");

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(12L)
                .username("john@mail.com")
                .firstName("John")
                .lastName("Doe")
                .password("encoded-password")
                .admin(true)
                .build();
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

        when(authenticationManager.authenticate(org.mockito.ArgumentMatchers.any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.of(User.builder()
                .email("john@mail.com")
                .firstName("John")
                .lastName("Doe")
                .password("encoded-password")
                .admin(true)
                .build()));

        JwtResponse response = authService.authenticate(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals(12L, response.getId());
        assertEquals("john@mail.com", response.getUsername());
        assertTrue(Boolean.TRUE.equals(response.getAdmin()));
        SecurityContextHolder.clearContext();
    }
}
