package com.telerikacademy.web.cryptocurrency_trading_platform.services;

import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.EntityNotFoundException;
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
    public Transaction createIncomingTransaction(User user, Transaction transaction2) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null");
        }

        if (transaction2.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        Transaction transaction = new Transaction();
        transaction.setCurrency(transaction2.getCurrency());
        transaction.setAmount(transaction2.getAmount());
        transaction.setUser(user);
        transaction.setStatus(Status.SELL);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setPrice(transaction2.getPrice());

        transactionRepository.save(transaction);

        return transaction;
    }

    @Override
    public Transaction createOutgoingTransaction(User user, Transaction transaction2) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null");
        }

        if (transaction2.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(transaction2.getAmount());
        transaction.setCurrency(transaction2.getCurrency());
        transaction.setUser(user);
        transaction.setStatus(Status.BUY);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setPrice(transaction2.getPrice());

        user.setBalance(user.getBalance() - transaction.getAmount());
        userRepository.save(user);

        transactionRepository.save(transaction);

        return transaction;
    }

    @Override
    public List<Transaction> filterTransactions(LocalDateTime startDate,
                                                LocalDateTime endDate,
                                                String currency,
                                                User user,
                                                Status status) {
        return transactionRepository.findAll().stream()
                .filter(t -> startDate == null || t.getCreatedAt().isAfter(startDate))
                .filter(t -> endDate == null || t.getCreatedAt().isBefore(endDate))
                .filter(t -> currency == null || t.getCurrency().equalsIgnoreCase(currency))
                .filter(t -> user == null || t.getUser().getEmail().equalsIgnoreCase(user.getEmail()))
                .filter(t -> status == null || t.getStatus().equals(status))
                .toList();
    }

    @Override
    public List<TransactionDTO> sortTransactions(List<TransactionDTO> transactions, String sortBy, boolean ascending) {
        return List.of();
    }

    @Override
    public Transaction findTransactionById(Long id) {
        Transaction transaction = transactionRepository.findTransactionById(id);
        if (transaction == null) {
            throw new EntityNotFoundException("Transaction", id);
        }
        return transaction;
    }

    @Override
    public Transaction createTransactionFromAmount(Double amount, User user, Transaction transaction2){
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setUser(user);
        transaction.setStatus(Status.SELL);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setCurrency(transaction2.getCurrency());
        transaction.setPrice(transaction2.getPrice());
        return transaction;
    }
}
