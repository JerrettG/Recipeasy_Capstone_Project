package com.kenzie.capstone.service.exception;

public class ProfileNotFoundException extends Exception{
    public ProfileNotFoundException() {
    }

    public ProfileNotFoundException(String message) {
        super(message);
    }
}
