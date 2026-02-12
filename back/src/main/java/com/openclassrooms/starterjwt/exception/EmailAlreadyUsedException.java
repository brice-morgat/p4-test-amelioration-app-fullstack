package com.openclassrooms.starterjwt.exception;

public class EmailAlreadyUsedException extends BadRequestException {
    public EmailAlreadyUsedException() {
        super("Error: Email is already taken!");
    }
}
