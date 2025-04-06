package com.telerikacademy.web.cryptocurrency_trading_platform.services;

import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.TransactionDTO;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {

    Transaction createIncomingTransaction(User user, Transaction transaction2);

    Transaction createOutgoingTransaction(User user, Transaction transaction2);

    List<Transaction> filterTransactions(LocalDateTime startDate,
                                         LocalDateTime endDate,
                                         String currency,
                                         User user,
                                         Status status);

    List<TransactionDTO> sortTransactions(List<TransactionDTO> transactions,
                                          String sortBy,
                                          boolean ascending);

    Transaction findTransactionById(Long id);

    Transaction createTransactionFromAmount(Double amount, User user, Transaction transaction2, Double openAmount);

    void deleteAllByUser(User user);
}
