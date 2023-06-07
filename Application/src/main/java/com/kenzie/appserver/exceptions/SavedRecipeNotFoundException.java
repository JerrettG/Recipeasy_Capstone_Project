package com.kenzie.appserver.exceptions;

public class SavedRecipeNotFoundException extends Exception{
    public SavedRecipeNotFoundException() {
        super();
    }

    public SavedRecipeNotFoundException(String message) {
        super(message);
    }
}
