package com.telerikacademy.web.cryptocurrency_trading_platform.services;

import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.TransactionDTO;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.TransactionRepository;
import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Transaction createIncomingTransaction(User user, Double amount) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setUser(user);
        transaction.setStatus(Status.INCOMING);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        return transaction;
    }

    @Override
    public Transaction createOutgoingTransaction(User user, Double amount) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setUser(user);
        transaction.setStatus(Status.OUTGOING);
        transaction.setCreatedAt(LocalDateTime.now());

        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);

        transactionRepository.save(transaction);

        return transaction;
    }

    @Override
    public List<Transaction> filterTransactions(LocalDateTime startDate, LocalDateTime endDate, String currency) {
        return List.of();
    }

    @Override
    public List<TransactionDTO> sortTransactions(List<TransactionDTO> transactions, String sortBy, boolean ascending) {
        return List.of();
    }
}
