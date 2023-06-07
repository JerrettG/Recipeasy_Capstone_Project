package com.kenzie.appserver.exceptions;

public class SavedRecipeAlreadyExistsException extends Exception{

    public SavedRecipeAlreadyExistsException() {
        super();
    }

    public SavedRecipeAlreadyExistsException(String message) {
        super(message);
    }
}
