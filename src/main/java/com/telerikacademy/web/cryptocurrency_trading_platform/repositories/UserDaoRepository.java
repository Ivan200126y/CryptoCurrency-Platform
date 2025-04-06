package com.telerikacademy.web.cryptocurrency_trading_platform.repositories;

import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;

import java.util.List;

public interface UserDaoRepository {

    User findUserById(Long id);

    User findByUsername(String username);

    User findByEmail(String email);

    User findByPhoneNumber(String phone);

    List<User> findAll();

    void save(User user);

    void delete(User user);
}
