package com.telerikacademy.web.cryptocurrency_trading_platform.services;

import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.DuplicateEntityException;
import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.EntityNotFoundException;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static com.telerikacademy.web.cryptocurrency_trading_platform.helpers.PermissionHelper.checkIfCreatorOrAdminForUser;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getById(int id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new EntityNotFoundException("User", id);
        }
        return user;
    }

    @Override
    public User getByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("User", "email", email);
        }
        return user;
    }

    @Override
    public User getByUsername(String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new EntityNotFoundException("User", "username", username);
        }
        return user;
    }

    @Override
    public User getByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber);
        if (user == null) {
            throw new EntityNotFoundException("User", "phone number", phoneNumber);
        }
        return user;
    }

    @Override
    public void alterAdminPermissions(long id, User user, boolean isAdmin) {
        if (user.isAdmin()) {
            user.setAdmin(false);
        } else {
            user.setAdmin(true);
        }
        userRepository.save(user);
    }

    @Override
    public void alterBlockPermissions(long id, User user, boolean isBlocked) {
        if (user.isBlocked()) {
            user.setBlocked(false);
        } else {
            user.setBlocked(true);
        }
        userRepository.save(user);
    }

    @Override
    public void create(User user) {
        boolean exists = userRepository.findByEmail(user.getEmail()) != null;
        if (exists) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }
        userRepository.save(user);
    }

    @Override
    public void update(User user, User doesUpdate) {
        checkIfCreatorOrAdminForUser(user, doesUpdate);
        boolean exists = userRepository.findByEmail(user.getEmail()) != null;
        if (exists) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }
        userRepository.save(user);
    }

    @Override
    public void delete(int id, User doesDelete) {
        User user = userRepository.findById(id);
        checkIfCreatorOrAdminForUser(user, doesDelete);
        userRepository.delete(user);
    }
}
