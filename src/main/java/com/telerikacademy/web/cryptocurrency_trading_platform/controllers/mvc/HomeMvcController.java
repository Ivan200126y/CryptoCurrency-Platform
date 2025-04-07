package com.telerikacademy.web.cryptocurrency_trading_platform.controllers.mvc;

import com.telerikacademy.web.cryptocurrency_trading_platform.CryptoPricesFetch;
import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.AuthenticationFailureException;
import com.telerikacademy.web.cryptocurrency_trading_platform.helpers.AuthenticationHelper;
import com.telerikacademy.web.cryptocurrency_trading_platform.mappers.TransactionMapper;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.OpenTransaction;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final CryptoPricesFetch cryptoPricesFetch;

    public HomeMvcController(UserService userService, AuthenticationHelper authenticationHelper, TransactionService transactionService, TransactionMapper transactionMapper, CryptoPricesFetch cryptoPricesFetch) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
        this.cryptoPricesFetch = cryptoPricesFetch;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping
    public String home(Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);

            List<Transaction> transactions = transactionService.filterTransactions(
                    null, null, null, user, null, null);

            Integer countTransactions = transactions.size();

            Map<String, List<Transaction>> totalOutgoing = transactions
                    .stream()
                    .filter(t -> t.getStatus() == Status.BUY)
                    .collect(Collectors.groupingBy(Transaction::getCurrency));

            Map<String, Double> totalIncoming = transactions
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
                    open.setAmount(openAmount);
                    open.setStatus(Status.BUY);
                    open.setUser(user);
                    open.setCreatedAt(LocalDateTime.now());
                    open.setPrice(avgPurchasePrice);
                    open.setCurrentPrice(currentPrice);
                    open.setShares(openAmount / currentPrice);
                    open.setId(entry.getValue().get(0).getId());
                    openTransactions.add(open);
                }
            }

            model.addAttribute("currentUser", user);
            model.addAttribute("countTransactions", countTransactions);
            model.addAttribute("openTransactions", openTransactions);
            model.addAttribute("transactions", transactions);
            return "home";
        } catch (AuthenticationFailureException e) {
            return "AccessDenied";
        }
    }

    @GetMapping("/about")
    public String showAboutPage(){
        return "About";
    }
}
