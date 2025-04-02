package com.telerikacademy.web.cryptocurrency_trading_platform.mappers;

import com.telerikacademy.web.cryptocurrency_trading_platform.models.UserDtoOut;
import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final UserRepository userRepository;

    public UserMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
