package com.openclassrooms.starterjwt.exception;

import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBadRequest_shouldReturnBodyWhenMessageIsPresent() {
        ResponseEntity<?> response = handler.handleBadRequest(new BadRequestException("bad request"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("bad request", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void handleBadRequest_shouldReturnEmptyBodyWhenMessageIsBlank() {
        ResponseEntity<?> response = handler.handleBadRequest(new BadRequestException("   "));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handlers_shouldReturnExpectedStatusCodes() {
        assertEquals(HttpStatus.BAD_REQUEST, handler.handleMethodArgumentTypeMismatch().getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, handler.handleMethodArgumentNotValid().getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, handler.handleNotFound().getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, handler.handleUnauthorized().getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, handler.handleUnexpectedException().getStatusCode());
    }
}
