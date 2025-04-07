package com.telerikacademy.web.cryptocurrency_trading_platform.controllers.mvc;

import com.telerikacademy.web.cryptocurrency_trading_platform.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserMvcController {

    UserService userService;

    public UserMvcController(UserService userService) {
        this.userService = userService;
    }

}
