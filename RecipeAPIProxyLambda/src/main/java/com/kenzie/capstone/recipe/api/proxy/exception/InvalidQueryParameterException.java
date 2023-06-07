package com.kenzie.capstone.recipe.api.proxy.exception;

public class InvalidQueryParameterException extends RuntimeException {

    public InvalidQueryParameterException() {
        super();
    }

    public InvalidQueryParameterException(String message) {
        super(message);

    }
}
