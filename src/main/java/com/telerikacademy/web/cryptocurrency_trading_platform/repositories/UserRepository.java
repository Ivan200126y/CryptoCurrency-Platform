package com.telerikacademy.web.cryptocurrency_trading_platform.repositories;

import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import org.hibernate.query.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    User findByUsername(String username);
    User findByEmail(String email);
    User findById(int id);
    User findByPhoneNumber(String phone);

}
