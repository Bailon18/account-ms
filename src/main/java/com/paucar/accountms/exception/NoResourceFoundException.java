package com.paucar.accountms.exception;

public class NoResourceFoundException extends RuntimeException {
    public NoResourceFoundException(String message) {
        super(message);
    }
}