package com.telerikacademy.web.cryptocurrency_trading_platform.controllers.mvc;

import com.telerikacademy.web.cryptocurrency_trading_platform.CryptoPricesFetch;
import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.EntityNotFoundException;
import com.telerikacademy.web.cryptocurrency_trading_platform.helpers.AuthenticationHelper;
import com.telerikacademy.web.cryptocurrency_trading_platform.mappers.TransactionMapper;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.OpenTransaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.TransactionDtoCreate;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import com.telerikacademy.web.cryptocurrency_trading_platform.services.TransactionService;
import com.telerikacademy.web.cryptocurrency_trading_platform.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/transactions")
public class TransactionMvcController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final CryptoPricesFetch cryptoPricesFetch;
    private final TransactionMapper transactionMapper;
    private double openAmount;

    public TransactionMvcController(TransactionService transactionService,
                                    UserService userService,
                                    AuthenticationHelper authenticationHelper, CryptoPricesFetch cryptoPricesFetch, TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.cryptoPricesFetch = cryptoPricesFetch;
        this.transactionMapper = transactionMapper;
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

        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "AccessDenied";
        }
        model.addAttribute("transaction", new TransactionDtoCreate());
        model.addAttribute("user", user);
        return "CreateTransaction";
    }

    @PostMapping("/new")
    public String createTransaction(@Valid @ModelAttribute TransactionDtoCreate transactionDtoCreate,
                                    @RequestParam(required = false) String type,
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

        Optional<Double> priceCrypto = cryptoPricesFetch.getPriceForSymbol(transactionDtoCreate.getCurrency());
        transactionDtoCreate.setPriceAtPurchase(priceCrypto.get());
        //Double totalUnits = Double.parseDouble(priceCrypto.get()) / amount;

        Transaction transaction = transactionMapper.fromTransactionDTo(transactionDtoCreate);

        switch (transactionDtoCreate.getType()) {
            case "sell":
                user.setBalance(user.getBalance() + amount);
                userService.update(user, user, user.getId());
                transactionService.createIncomingTransaction(user, transaction);
                break;
            default:
                if (user.getBalance() < amount) {
                    errors.rejectValue("amount", "error.amount.over");
                    return "CreateTransaction";
                }
                user.setBalance(user.getBalance() - amount);
                userService.update(user, user, user.getId());
                transactionService.createOutgoingTransaction(user, transaction);
                break;
        }
        return "redirect:/transactions/all";
    }

    @GetMapping("/current")
    public String showCurrentTransactions(Model model, HttpSession session) {

        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "AccessDenied";
        }

        List<Transaction> allUserTransactions =
                transactionService.filterTransactions(null, null, null, user, null);

        Map<String, List<Transaction>> totalOutgoing = allUserTransactions
                .stream()
                .filter(t -> t.getStatus() == Status.BUY)
                .collect(Collectors.groupingBy(Transaction::getCurrency));

        Map<String, Double> totalIncoming = allUserTransactions
                .stream()
                .filter(t -> t.getStatus() == Status.SELL)
                .collect(Collectors.groupingBy(Transaction::getCurrency,
                        Collectors.summingDouble(Transaction::getAmount)));

        List<OpenTransaction> openTransactions = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : totalOutgoing.entrySet()) {
            String currency = entry.getKey();
            List<Transaction> outgoingTr = entry.getValue();

            double totalOut = outgoingTr.stream().mapToDouble(Transaction::getAmount).sum();
            Double inAmount = totalIncoming.getOrDefault(currency, 0.0);

            openAmount = totalOut - inAmount;

            if (openAmount > 0) {
                OpenTransaction open = new OpenTransaction();
                Double avgPurchasePrice = outgoingTr
                        .stream()
                        .mapToDouble(t -> t.getPrice())
                        .average()
                        .orElse(0.0);

                Double currentPrice = cryptoPricesFetch.getPriceForSymbol(currency).get();
                open.setCurrency(currency);
                open.setAmount(openAmount);
                open.setStatus(Status.BUY);
                open.setUser(user);
                open.setCreatedAt(LocalDateTime.now());
                open.setPrice(avgPurchasePrice);
                open.setCurrentPrice(currentPrice);
                open.setId(entry.getValue().get(0).getId());
                openTransactions.add(open);
            }
        }
        model.addAttribute("transactions", openTransactions);
        return "CurrentHoldings";
    }

    @GetMapping("/close/{id}")
    public String showCloseButton(@PathVariable Long id, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "AccessDenied";
        }

        Transaction transaction = transactionService.findTransactionById(id);

        Transaction openTransaction = transactionService
                .createTransactionFromAmount(openAmount, user, transaction);
        openTransaction.setStatus(Status.SELL);

        transactionService.createIncomingTransaction(user, openTransaction);
        return "redirect:/";
    }
    //buy and sell crypto
    //view transaction history
}
