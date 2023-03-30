package com.dws.challenge.exception;

public class OverdraftNotAllowedException extends RuntimeException{

    public OverdraftNotAllowedException(String message) {
        super(message);
    }
}
