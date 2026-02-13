package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {

    private static final String SECRET = "test-secret-key-for-jwt-hs512-minimum-length-64-characters-abcdef";

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 60_000);
    }

    @Test
    void generateJwtToken_shouldReturnValidTokenAndUsername() {
        String token = jwtUtils.generateJwtToken(authenticationFor("john@mail.com"));

        assertNotNull(token);
        assertEquals("john@mail.com", jwtUtils.getUserNameFromJwtToken(token));
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldReturnFalseForExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
                .setSubject("john@mail.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10_000))
                .setExpiration(new Date(System.currentTimeMillis() - 1_000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(expiredToken));
    }

    @Test
    void validateJwtToken_shouldReturnFalseForInvalidSignature() {
        SecretKey otherKey = Keys.hmacShaKeyFor("another-valid-hs512-secret-key-with-at-least-64-characters-abcdef".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .setSubject("john@mail.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(otherKey, SignatureAlgorithm.HS512)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldReturnFalseForMalformedOrEmptyToken() {
        assertFalse(jwtUtils.validateJwtToken("not-a-jwt"));
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    private Authentication authenticationFor(String username) {
        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(1L)
                .username(username)
                .firstName("John")
                .lastName("Doe")
                .password("pwd")
                .admin(false)
                .build();

        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }
}
