package com.telerikacademy.web.cryptocurrency_trading_platform.repositories;

import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoRepositoryImpl implements UserDaoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> rowMapper;

    @Autowired
    public UserDaoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setBlocked(rs.getBoolean("isBlocked"));
            user.setAdmin(rs.getBoolean("isAdmin"));
            user.setBalance(rs.getDouble("balance"));
            user.setEmail(rs.getString("email"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setUsername(rs.getString("username"));
            user.setPhoneNumber(rs.getString("phone"));
            user.setPassword(rs.getString("password"));
            return user;
        };
    }

    @Override
    public User findUserById(Long id) {
        String sql = "select * from users where id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    @Override
    public User findByUsername(String username) {
        String sql = "select * from users where username = ?";
        List<User> users = jdbcTemplate.query(sql, rowMapper, username);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public User findByEmail(String email) {
        String sql = "select * from users where email = ?";
        //List<User> users = jdbcTemplate.queryForObject(sql, rowMapper, email);
        return jdbcTemplate.queryForObject(sql, rowMapper, email);
    }

    @Override
    public User findByPhoneNumber(String phone) {
        return jdbcTemplate.queryForObject("select * from users where phone = ?", rowMapper, phone);
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("select * from users", rowMapper);
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (id, isBlocked, isAdmin, balance, email, first_name, last_name, username, phone, password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "isBlocked = VALUES(isBlocked), " +
                "isAdmin = VALUES(isAdmin), " +
                "balance = VALUES(balance), " +
                "email = VALUES(email), " +
                "first_name = VALUES(first_name), " +
                "last_name = VALUES(last_name), " +
                "username = VALUES(username), " +
                "phone = VALUES(phone), " +
                "password = VALUES(password)";


        jdbcTemplate.update(sql,
                user.getId(),
                user.isBlocked(),
                user.isAdmin(),
                user.getBalance(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getPhoneNumber(),
                user.getPassword());
    }

    @Override
    public void delete(User user) {
        jdbcTemplate.update("Delete * from users where id=?", user.getId());
    }
}
