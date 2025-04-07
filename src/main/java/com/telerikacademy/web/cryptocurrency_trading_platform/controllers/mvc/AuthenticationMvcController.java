package com.telerikacademy.web.cryptocurrency_trading_platform.controllers.mvc;

import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.DuplicateEntityException;
import com.telerikacademy.web.cryptocurrency_trading_platform.helpers.AuthenticationHelper;
import com.telerikacademy.web.cryptocurrency_trading_platform.mappers.UserMapper;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.LogInDto;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.RegisterDto;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.UserDtoOut;
import com.telerikacademy.web.cryptocurrency_trading_platform.services.TransactionService;
import com.telerikacademy.web.cryptocurrency_trading_platform.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthenticationMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final TransactionService transactionService;

    @Autowired
    public AuthenticationMvcController(AuthenticationHelper authenticationHelper,
                                       UserService userService,
                                       UserMapper userMapper, TransactionService transactionService) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.transactionService = transactionService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("register", new RegisterDto());
        return "Register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("register") RegisterDto registerDto,
                                  BindingResult errors) {

        if (errors.hasErrors()) {
            return "Register";
        }

        if (!registerDto.getPassword().equals(registerDto.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm", "password.mismatch", "Passwords do not match.");
            return "Register";
        }

        try {
            userService.getByPhoneNumber(registerDto.getPhone());
            errors.rejectValue("phone", "phone.duplicate", "Phone number already exists.");
            return "Register";
        } catch (Exception e) {
        }

        try {
            userService.getByEmail(registerDto.getEmail());
            errors.rejectValue("email", "email.duplicate", "Email already exists.");
            return "Register";
        } catch (Exception e) {
        }

        try {
            userService.getByUsername(registerDto.getUsername());
            errors.rejectValue("username", "username.duplicate", "Username already exists.");
            return "Register";
        } catch (Exception e) {
        }

        try {
            User user = userMapper.fromRegisterDto(registerDto);
            userService.create(user);
            return "redirect:/auth/login";
        } catch (DuplicateEntityException e) {
            errors.rejectValue("username", "duplicate.username",
                    "Username is already taken!");
            return "Register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new LogInDto());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("login") LogInDto login,
                               BindingResult errors,
                               HttpSession session) {
        if (errors.hasErrors()) {
            return "login";
        }

        User user;
        try {
            user = authenticationHelper.verifyAuthentication(login.getUsername(), login.getPassword());
            session.setAttribute("currentUser", user.getUsername());
            session.setAttribute("isAdmin", user.isAdmin());
            return "redirect:/";
        } catch (AuthenticationFailureException e) {
            errors.reject("Invalid authentication");
            return "login";
        } catch (EntityNotFoundException e) {
            errors.reject("invalid.credentials", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/account/reset")
    public String reset(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "AccessDenied";
        }
        user.setBalance(1000);
        userService.update(user, user, user.getId());
        transactionService.deleteAllByUser(user);
        model.addAttribute("currentUser", user);
        return "home";
    }

    @GetMapping("/account")
    public String showAccountPage(Model model,
                                  HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            UserDtoOut userDtoOut = userMapper.fromUserToDtoOut(user);
            model.addAttribute("user", userDtoOut);
            return "Account";
        } catch (AuthenticationFailureException e) {
            return "AccessDenied";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
