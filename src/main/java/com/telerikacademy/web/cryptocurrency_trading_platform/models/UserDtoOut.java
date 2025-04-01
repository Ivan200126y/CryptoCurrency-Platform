package com.telerikacademy.web.cryptocurrency_trading_platform.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoOut {

    private long id;
    private String username;
    private String email;
    private String phone;
}
