package com.telerikacademy.web.cryptocurrency_trading_platform.mappers;

import com.telerikacademy.web.cryptocurrency_trading_platform.CryptoPricesFetch;
import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.OpenTransaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.TransactionDtoCreate;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionMapper {

    private final CryptoPricesFetch cryptoPricesFetch;

    public TransactionMapper(CryptoPricesFetch cryptoPricesFetch) {
        this.cryptoPricesFetch = cryptoPricesFetch;
    }

    public Transaction fromTransactionDTo(TransactionDtoCreate transactionDtoCreate) {
        Transaction transaction = new Transaction();
        transaction.setAmount(Double.parseDouble(transactionDtoCreate.getAmount()));
        transaction.setCurrency(transactionDtoCreate.getCurrency());
        transaction.setPrice(transactionDtoCreate.getPriceAtPurchase());
        return transaction;
    }

    public OpenTransaction fromTransaction(Transaction transaction) {
        OpenTransaction openTransaction = new OpenTransaction();
        openTransaction.setAmount(transaction.getAmount());
        openTransaction.setCurrency(transaction.getCurrency());
        openTransaction.setPrice(transaction.getPrice());
        openTransaction.setUser(transaction.getUser());
        openTransaction.setId(transaction.getId());
        openTransaction.setStatus(transaction.getStatus());
        openTransaction.setCurrentPrice(cryptoPricesFetch.getPriceForSymbol(transaction.getCurrency()).get());
        return openTransaction;
    }

    public Transaction fromOpenTransaction(OpenTransaction openTransaction) {
        Transaction transaction = new Transaction();
        transaction.setUser(openTransaction.getUser());
        transaction.setAmount(openTransaction.getAmount());
        transaction.setStatus(openTransaction.getStatus());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setCurrency(openTransaction.getCurrency());
        transaction.setPrice(openTransaction.getPrice());
        return transaction;
    }

}
