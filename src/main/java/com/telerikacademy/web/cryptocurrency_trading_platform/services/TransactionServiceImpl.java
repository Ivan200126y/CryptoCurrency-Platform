package com.telerikacademy.web.cryptocurrency_trading_platform.services;

import com.telerikacademy.web.cryptocurrency_trading_platform.CryptoPricesFetch;
import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.EntityNotFoundException;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.TransactionDTO;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.TransactionDaoRepository;
//import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.TransactionRepository;
import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.UserDaoRepository;
//import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionDaoRepository transactionRepository;
    private final UserDaoRepository userRepository;
    private final CryptoPricesFetch cryptoPricesFetch;

    @Autowired
    public TransactionServiceImpl(TransactionDaoRepository transactionRepository,
                                  UserDaoRepository userRepository, CryptoPricesFetch cryptoPricesFetch) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.cryptoPricesFetch = cryptoPricesFetch;
    }

    @Override
    public Transaction createIncomingTransaction(User user, Transaction transaction2) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null");
        }

        if (transaction2.getAmount() > user.getBalance()) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        double priceCrypto = cryptoPricesFetch.getPriceForSymbol(transaction2.getCurrency()).get();

        Transaction transaction = new Transaction();
        transaction.setCurrency(transaction2.getCurrency());
        transaction.setAmount(transaction2.getShares() * priceCrypto);
        transaction.setUser(user);
        transaction.setStatus(Status.SELL);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setPrice(transaction2.getPrice());
        transaction.setShares(transaction2.getShares());
        transactionRepository.save(transaction);

        user.setBalance(user.getBalance() + transaction.getAmount());
        userRepository.save(user);

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

        if (transaction2.getAmount() > user.getBalance()) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(transaction2.getAmount());
        transaction.setCurrency(transaction2.getCurrency());
        transaction.setUser(user);
        transaction.setStatus(Status.BUY);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setPrice(transaction2.getPrice());
        transaction.setShares(transaction2.getShares());

        transactionRepository.save(transaction);

        user.setBalance(user.getBalance() - transaction.getAmount());
        userRepository.save(user);

        return transaction;
    }

    @Override
    public List<Transaction> filterTransactions(LocalDateTime startDate,
                                                LocalDateTime endDate,
                                                String currency,
                                                User user,
                                                Status status,
                                                Boolean transactionType) {
        return transactionRepository.findAll().stream()
                .filter(t -> startDate == null || t.getCreatedAt().isAfter(startDate))
                .filter(t -> endDate == null || t.getCreatedAt().isBefore(endDate))
                .filter(t -> currency == null || t.getCurrency().equalsIgnoreCase(currency))
                .filter(t -> user == null || t.getUser().getEmail().equalsIgnoreCase(user.getEmail()))
                .filter(t -> status == null || t.getStatus().equals(status))
                .filter(t -> {
                    if (transactionType == null) return true;
                    boolean transactionIsSell = t.getStatus() == Status.SELL;
                    return transactionType == transactionIsSell;
                })
                .toList();
    }

    @Override
    public Page<Transaction> sortTransactionsWithPagination(List<Transaction> transactions,
                                                            String sortBy,
                                                            boolean ascending,
                                                            int page,
                                                            int size) {
        Comparator<Transaction> comparator = switch (sortBy.toLowerCase()) {
            case "amount" -> Comparator.comparing(Transaction::getAmount);
            case "date" -> Comparator.comparing(Transaction::getCreatedAt);
            default -> throw new IllegalStateException("Unexpected value: " + sortBy);
        };

        if (!ascending) {
            comparator = comparator.reversed();
        }


        List<Transaction> sortedTransactions = transactions.stream()
                .sorted(comparator)
                .collect(Collectors.toList());


        Pageable pageable = PageRequest.of(page, size);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedTransactions.size());

        List<Transaction> paginatedTransactions = sortedTransactions.subList(start, end);

        return new PageImpl<>(paginatedTransactions, pageable, sortedTransactions.size());
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
    public Transaction createTransactionFromAmount(Double amount, User user, Transaction transaction2, Double openAmount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(openAmount);
        transaction.setUser(user);
        transaction.setStatus(Status.SELL);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setCurrency(transaction2.getCurrency());
        transaction.setPrice(transaction2.getPrice());
        cryptoPricesFetch.getPriceForSymbol(transaction2.getCurrency());
        Double totalShares = amount / transaction2.getPrice();
        transaction.setShares(totalShares);
        return transaction;
    }

    @Override
    public void deleteAllByUser(User user) {
        List<Transaction> userTransactions =
                filterTransactions(null, null, null, user, null, null);
        for (Transaction transaction : userTransactions) {
            transactionRepository.delete(transaction);
        }
    }
}
