package com.kenzie.capstone.service.exception;

public class ProfileAlreadyExistsException extends Exception {
    public ProfileAlreadyExistsException() {
    }

    public ProfileAlreadyExistsException(String message) {
        super(message);
    }
}
