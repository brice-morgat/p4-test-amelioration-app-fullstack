package com.openclassrooms.starterjwt.security.jwt;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    private AuthTokenFilter filter;
    private Logger authTokenFilterLogger;
    private Level previousLogLevel;

    @BeforeEach
    void setUp() {
        filter = new AuthTokenFilter();
        ReflectionTestUtils.setField(filter, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(filter, "userDetailsService", userDetailsService);
        authTokenFilterLogger = (Logger) LoggerFactory.getLogger(AuthTokenFilter.class);
        previousLogLevel = authTokenFilterLogger.getLevel();
        authTokenFilterLogger.setLevel(Level.OFF);
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
        authTokenFilterLogger.setLevel(previousLogLevel);
    }

    @Test
    void doFilterInternal_shouldContinueWithoutAuthenticationWhenHeaderIsMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(jwtUtils, userDetailsService);
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldAuthenticateWhenTokenIsValid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("john@mail.com")
                .firstName("John")
                .lastName("Doe")
                .password("pwd")
                .admin(false)
                .build();

        when(jwtUtils.validateJwtToken("valid-token")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("valid-token")).thenReturn("john@mail.com");
        when(userDetailsService.loadUserByUsername("john@mail.com")).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }

    @Test
    void doFilterInternal_shouldNotAuthenticateWhenTokenIsInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.validateJwtToken("invalid-token")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldSwallowExceptionAndContinue() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.validateJwtToken("token")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("token")).thenThrow(new RuntimeException("boom"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
