package com.telerikacademy.web.cryptocurrency_trading_platform.controllers.mvc;

import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.EntityNotFoundException;
import com.telerikacademy.web.cryptocurrency_trading_platform.helpers.AuthenticationHelper;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.TransactionDtoCreate;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import com.telerikacademy.web.cryptocurrency_trading_platform.services.TransactionService;
import com.telerikacademy.web.cryptocurrency_trading_platform.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionMvcController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    public TransactionMvcController(TransactionService transactionService,
                                    UserService userService,
                                    AuthenticationHelper authenticationHelper) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/all")
    public String showtransactions(Model model,
                                   HttpSession session,
                                   @RequestParam(required = false, defaultValue = "0") int page,
                                   @RequestParam(required = false, defaultValue = "5") int size,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                   @RequestParam(required = false) Double amount,
                                   @RequestParam(required = false) Status status,
                                   @RequestParam(required = false) String currency) {
        User user = authenticationHelper.tryGetUser(session);
        session.setAttribute("currentUser", user.getUsername());
        List<Transaction> transactions = transactionService.filterTransactions(null, null, null, user, null);
        session.setAttribute("currentUser", user.getUsername());
        model.addAttribute("transactions", transactions);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("amount", amount);
        model.addAttribute("status", status);
        model.addAttribute("currency", currency);
        return "TransactionsView";
    }

    @GetMapping("/new")
    public String showTransactionForm(Model model, HttpSession session) {

        try {
            authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "AccessDenied";
        }
        model.addAttribute("transaction", new TransactionDtoCreate());
        return "CreateTransaction";
    }

    @PostMapping("/new")
    public String createTransaction(@Valid @ModelAttribute TransactionDtoCreate transactionDtoCreate,
                                    BindingResult errors,
                                    HttpSession session) {
        Double amount = Double.parseDouble(transactionDtoCreate.getAmount());

        if (errors.hasErrors()) {
            return "CreateTransaction";
        }

        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
            if (user.isBlocked()) {
                return "BlockedView";
            }
        } catch (AuthenticationFailureException e) {
            return "AccessDenied";
        } catch (EntityNotFoundException e) {
            return "CreateTransaction";
        }

        switch (transactionDtoCreate.getType()) {
            case "buy":
                if (user.getBalance() < amount) {
                    errors.rejectValue("amount", "error.amount.over");
                    return "CreateTransaction";
                }
                user.setBalance(user.getBalance() - amount);
                userService.update(user, user);
                transactionService.createOutgoingTransaction(user, Double.parseDouble(transactionDtoCreate.getAmount()));
                break;
            case "sell":
                user.setBalance(user.getBalance() + amount);
                userService.update(user, user);
                transactionService.createIncomingTransaction(user, Double.parseDouble(transactionDtoCreate.getAmount()));
                break;
        }

        return "redirect:/transactions/all";
    }

    //buy and sell crypto
    //view transaction history
}
