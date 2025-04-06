package com.telerikacademy.web.cryptocurrency_trading_platform.services;

import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;

public interface UserService {

    User getById(Long id);

    User getByEmail(String email);

    User getByUsername(String username);

    User getByPhoneNumber(String phoneNumber);

    void create(User user);

    void update(User user, User doesUpdate, Long id);

    void delete(Long id, User doesDelete);

}
