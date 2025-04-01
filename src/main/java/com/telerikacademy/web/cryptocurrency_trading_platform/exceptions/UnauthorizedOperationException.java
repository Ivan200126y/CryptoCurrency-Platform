package com.telerikacademy.web.cryptocurrency_trading_platform.exceptions;

public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
