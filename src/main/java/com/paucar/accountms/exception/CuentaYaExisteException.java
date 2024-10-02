package com.paucar.accountms.exception;

public class CuentaYaExisteException extends RuntimeException {
    public CuentaYaExisteException(String message) {
        super(message);
    }
}
