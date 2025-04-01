package com.telerikacademy.web.cryptocurrency_trading_platform.repositories;

import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

}
