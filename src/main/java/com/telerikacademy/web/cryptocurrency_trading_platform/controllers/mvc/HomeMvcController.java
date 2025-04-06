package com.telerikacademy.web.cryptocurrency_trading_platform.controllers.mvc;

import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.cryptocurrency_trading_platform.helpers.AuthenticationHelper;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import com.telerikacademy.web.cryptocurrency_trading_platform.services.TransactionService;
import com.telerikacademy.web.cryptocurrency_trading_platform.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final TransactionService transactionService;

    public HomeMvcController(UserService userService, AuthenticationHelper authenticationHelper, TransactionService transactionService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.transactionService = transactionService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping
    public String home(Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Integer countTransactions = (Integer) transactionService.filterTransactions(
                            null, null, null, user, null)
                    .size();
            List<Transaction> transactions = transactionService.filterTransactions(
                    null, null, null, user, null);
            model.addAttribute("currentUser", user);
            model.addAttribute("countTransactions", countTransactions);
            model.addAttribute("transactions", transactions);
            return "home";
        } catch (AuthenticationFailureException e) {
            return "AccessDenied";
        }
    }
}
