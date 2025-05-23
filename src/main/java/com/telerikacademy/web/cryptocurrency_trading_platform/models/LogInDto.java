package com.telerikacademy.web.cryptocurrency_trading_platform.models;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogInDto {

    @NotEmpty(message = "Username can't be empty!")
    private String username;

    @NotEmpty(message = "Password can't be empty!")
    private String password;
}
