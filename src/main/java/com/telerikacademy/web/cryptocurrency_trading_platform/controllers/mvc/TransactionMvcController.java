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
import org.springdoc.core.service.OpenAPIService;
import org.springframework.data.domain.Page;
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
    private final OpenAPIService openAPIService;

    public TransactionMvcController(TransactionService transactionService,
                                    UserService userService,
                                    AuthenticationHelper authenticationHelper, CryptoPricesFetch cryptoPricesFetch, TransactionMapper transactionMapper, OpenAPIService openAPIService) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.cryptoPricesFetch = cryptoPricesFetch;
        this.transactionMapper = transactionMapper;
        this.openAPIService = openAPIService;
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
                                   @RequestParam(required = false) Status status,
                                   @RequestParam(required = false) String currency,
                                   @RequestParam(required = false) boolean isAscending,
                                   @RequestParam(required = false) String sortBy,
                                   @RequestParam(required = false) Boolean transactionType) {
        User user = authenticationHelper.tryGetUser(session);
        Page<Transaction> paginatedTransactions;
        try {
            if (sortBy == null) {
                sortBy = "amount";
            }

            List<Transaction> transactions = transactionService.filterTransactions(startDate, endDate, currency,
                    user, status, transactionType);

            paginatedTransactions = transactionService.sortTransactionsWithPagination(transactions, sortBy, isAscending, page, size);
        } catch (AuthenticationFailureException e) {
            return "AccessDenied";
        }

        session.setAttribute("currentUser", user.getUsername());

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("status", status);
        model.addAttribute("currency", currency);
        model.addAttribute("transactions", paginatedTransactions);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("isAscending", isAscending);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paginatedTransactions.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("transactionType", transactionType);

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
    public String createTransaction(@Valid @ModelAttribute("transaction") TransactionDtoCreate transactionDtoCreate,
                                    BindingResult errors,
                                    @RequestParam(required = false) String type,
                                    HttpSession session) {
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
        transactionDtoCreate.setShares(Double.parseDouble(transactionDtoCreate.getAmount()) / priceCrypto.get());
        Double amount = Double.parseDouble(transactionDtoCreate.getAmount());

        Transaction transaction = transactionMapper.fromTransactionDTo(transactionDtoCreate);
        if (user.getBalance() < amount) {
            errors.rejectValue("amount", "error.amount.over");
            return "CreateTransaction";
        }
        transactionService.createOutgoingTransaction(user, transaction);

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
                transactionService.filterTransactions(null, null, null, user, null, null);

        Map<String, List<Transaction>> totalOutgoing = allUserTransactions
                .stream()
                .filter(t -> t.getStatus() == Status.BUY)
                .collect(Collectors.groupingBy(Transaction::getCurrency));

        Map<String, Double> totalIncoming = allUserTransactions
                .stream()
                .filter(t -> t.getStatus() == Status.SELL)
                .collect(Collectors.groupingBy(Transaction::getCurrency,
                        Collectors.summingDouble(Transaction::getShares)));

        List<OpenTransaction> openTransactions = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : totalOutgoing.entrySet()) {
            String currency = entry.getKey();
            List<Transaction> outgoingTr = entry.getValue();

            double totalOut = outgoingTr.stream().mapToDouble(Transaction::getShares).sum();
            Double inAmount = totalIncoming.getOrDefault(currency, 0.0);

            double openAmount = totalOut - inAmount;

            if (openAmount > 0) {
                OpenTransaction open = new OpenTransaction();
                Double avgPurchasePrice = outgoingTr
                        .stream()
                        .mapToDouble(t -> t.getPrice())
                        .average()
                        .orElse(0.0);

                Double currentPrice = cryptoPricesFetch.getPriceForSymbol(currency).get();
                open.setCurrency(currency);
                open.setStatus(Status.BUY);
                open.setAmount(openAmount * currentPrice);
                open.setUser(user);
                open.setCreatedAt(LocalDateTime.now());
                open.setPrice(avgPurchasePrice);
                open.setCurrentPrice(currentPrice);
                open.setId(entry.getValue().get(0).getId());
                open.setShares(openAmount);
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
        List<Transaction> transactions = transactionService
                .filterTransactions(null, null, transaction.getCurrency(), user, null, null);
        Double total = 0.0;
        for (Transaction t : transactions) {
            total = total + t.getPrice();
        }
        Double avgPrice = total / transactions.size();
        transaction.setPrice(avgPrice);

        Double price = cryptoPricesFetch.getPriceForSymbol(transaction.getCurrency()).get();

        List<Transaction> allUserTransactions =
                transactionService.filterTransactions(null, null, null, user, null, null);

        double totalBought = allUserTransactions.stream()
                .filter(t -> t.getStatus() == Status.BUY && t.getCurrency().equals(transaction.getCurrency()))
                .mapToDouble(Transaction::getShares)
                .sum();

        double totalSold = allUserTransactions.stream()
                .filter(t -> t.getStatus() == Status.SELL && t.getCurrency().equals(transaction.getCurrency()))
                .mapToDouble(Transaction::getShares)
                .sum();

        double total$Bought = allUserTransactions.stream()
                .filter(t -> t.getStatus() == Status.BUY && t.getCurrency().equals(transaction.getCurrency()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double total$Sold = allUserTransactions.stream()
                .filter(t -> t.getStatus() == Status.SELL && t.getCurrency().equals(transaction.getCurrency()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double openShares = totalBought - totalSold;
        double openAmount = total$Bought - total$Sold;

        Transaction openTransaction = transactionService
                .createTransactionFromAmount(openShares, user, transaction, openAmount);
        openTransaction.setStatus(Status.SELL);
        openTransaction.setPrice(price);
        openTransaction.setShares(openShares);

        transactionService.createIncomingTransaction(user, openTransaction);
        return "redirect:/";
    }

    @GetMapping("/sell")
    public String showSellForm(@RequestParam String currency, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "AccessDenied";
        }
        TransactionDtoCreate dtoCreate = new TransactionDtoCreate();
        dtoCreate.setCurrency(currency);
        model.addAttribute("user", user);
        model.addAttribute("transaction", dtoCreate);
        model.addAttribute("currency", currency);
        return "CreateSellTransaction";
    }

    @PostMapping("/sell")
    public String createSellTransaction(
            @Valid @ModelAttribute("transaction") TransactionDtoCreate transactionDtoCreate,
            BindingResult errors,
            HttpSession session) {

        if (errors.hasErrors()) {
            return "CreateSellTransaction";
        }

        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
            if (user.isBlocked()) {
                return "BlockedView";
            }
        } catch (AuthenticationFailureException e) {
            return "AccessDenied";
        }

        String currency = transactionDtoCreate.getCurrency();

        List<Transaction> transactions = transactionService.filterTransactions(null, null, currency,
                user, null, null);

        double totalBought = transactions.stream()
                .filter(t -> t.getStatus() == Status.BUY)
                .mapToDouble(Transaction::getShares)
                .sum();

        double totalSold = transactions.stream()
                .filter(t -> t.getStatus() == Status.SELL)
                .mapToDouble(Transaction::getShares)
                .sum();

        double openAmount = totalBought - totalSold;

        if (transactionDtoCreate.getShares() <= 0 || transactionDtoCreate.getShares() > openAmount) {
            errors.rejectValue("shares", "shares.exceeds");
            return "CreateSellTransaction";
        }

        double currentPrice = cryptoPricesFetch.getPriceForSymbol(currency).get();
        double amount = transactionDtoCreate.getShares() * currentPrice;

        transactionDtoCreate.setPriceAtPurchase(currentPrice);
        transactionDtoCreate.setAmount(String.valueOf(amount));

        Transaction transaction = transactionMapper.fromTransactionDTo(transactionDtoCreate);
        transaction.setStatus(Status.SELL);

        transactionService.createIncomingTransaction(user, transaction);

        return "redirect:/transactions/all";
    }

}
