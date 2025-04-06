package com.telerikacademy.web.cryptocurrency_trading_platform.repositories;

import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;

import java.util.List;

public interface TransactionDaoRepository {

    Transaction findTransactionById(Long id);

    List<Transaction> findAll();

    void save(Transaction transaction);

    void delete(Transaction transaction);

    void deleteAllByUser(User user);
}
