package com.telerikacademy.web.cryptocurrency_trading_platform.mappers;

import com.telerikacademy.web.cryptocurrency_trading_platform.models.UserDtoOut;
import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.UserRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserMapper {

    private final UserRepository userRepository;


}
