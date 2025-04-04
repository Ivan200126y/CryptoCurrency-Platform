package com.telerikacademy.web.cryptocurrency_trading_platform.services;

import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;

public interface UserService {

    User getById(int id);

    User getByEmail(String email);

    User getByUsername(String username);

    User getByPhoneNumber(String phoneNumber);

    void alterAdminPermissions(long id, User user, boolean isAdmin);

    void alterBlockPermissions(long id, User user, boolean isBlocked);

    void create(User user);

    void update(User user, User doesUpdate, Long id);

    void delete(int id, User doesDelete);

}
