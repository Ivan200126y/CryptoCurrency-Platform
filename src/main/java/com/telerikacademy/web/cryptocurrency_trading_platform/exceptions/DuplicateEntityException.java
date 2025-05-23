package com.telerikacademy.web.cryptocurrency_trading_platform.exceptions;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String type, String attribute, String value) {
        super(String.format("%s with %s %s already exists!", type, attribute, value));
    }

    public DuplicateEntityException(String message) {
        super(message);
    }
}
