package com.telerikacademy.web.cryptocurrency_trading_platform.exceptions;

public class AuthenticationFailureException extends RuntimeException {
    public AuthenticationFailureException(String message) {
        super(message);
    }
}
