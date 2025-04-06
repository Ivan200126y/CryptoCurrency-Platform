package com.telerikacademy.web.cryptocurrency_trading_platform.mappers;

import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.TransactionDtoCreate;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.UserDtoOut;
import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.UserDaoRepository;
//import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final UserDaoRepository userRepository;

    public UserMapper(UserDaoRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDtoOut fromUserToDtoOut(User user) {
        UserDtoOut userDtoOut = new UserDtoOut();
        userDtoOut.setId(user.getId());
        userDtoOut.setUsername(user.getUsername());
        userDtoOut.setEmail(user.getEmail());
        userDtoOut.setPhone(user.getPhoneNumber());
        return userDtoOut;
    }
}
