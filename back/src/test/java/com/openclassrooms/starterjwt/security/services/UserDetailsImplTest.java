package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDetailsImplTest {

    @Test
    void userDetails_shouldReturnExpectedSecurityFlags() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("john@mail.com")
                .firstName("John")
                .lastName("Doe")
                .password("pwd")
                .admin(false)
                .build();

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        assertEquals(0, user.getAuthorities().size());
    }

    @Test
    void equals_shouldHandleAllComparisonBranches() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).username("john@mail.com").build();
        UserDetailsImpl sameId = UserDetailsImpl.builder().id(1L).username("other@mail.com").build();
        UserDetailsImpl differentId = UserDetailsImpl.builder().id(2L).username("john@mail.com").build();

        assertTrue(user.equals(user));
        assertFalse(user.equals(null));
        assertFalse(user.equals("not-a-user"));
        assertTrue(user.equals(sameId));
        assertFalse(user.equals(differentId));
    }
}
