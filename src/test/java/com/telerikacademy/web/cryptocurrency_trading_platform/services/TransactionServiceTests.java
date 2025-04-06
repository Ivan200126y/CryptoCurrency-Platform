package com.telerikacademy.web.cryptocurrency_trading_platform.services;

import com.telerikacademy.web.cryptocurrency_trading_platform.CryptoPricesFetch;
import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.EntityNotFoundException;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.TransactionRepository;
import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTests {

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CryptoPricesFetch cryptoPricesFetch;

    @InjectMocks
    TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createIncomingTransaction_Should_Succeed_WhenValid() {
        User user = new User();
        user.setBalance(100.0);

        Transaction inputTx = new Transaction();
        inputTx.setAmount(50.0);
        inputTx.setCurrency("BTC");
        inputTx.setShares(0.001);
        inputTx.setPrice(50000.0);

        when(cryptoPricesFetch.getPriceForSymbol("BTC")).thenReturn(Optional.of(50000.0));

        Transaction result = transactionService.createIncomingTransaction(user, inputTx);

        assertEquals(Status.SELL, result.getStatus());
        assertEquals("BTC", result.getCurrency());
        assertEquals(0.001 * 50000.0, result.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createIncomingTransaction_Should_Throw_WhenAmountIsZero() {
        User user = new User();
        Transaction tx = new Transaction();
        tx.setAmount(0.0);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> transactionService.createIncomingTransaction(user, tx));

        assertEquals("Amount must be greater than 0", ex.getMessage());
    }

    @Test
    void createOutgoingTransaction_Should_Succeed_WhenValid() {
        User user = new User();
        user.setBalance(1000.0);

        Transaction inputTx = new Transaction();
        inputTx.setAmount(200.0);
        inputTx.setCurrency("ETH");
        inputTx.setShares(0.2);
        inputTx.setPrice(1000.0);

        Transaction result = transactionService.createOutgoingTransaction(user, inputTx);

        assertEquals(Status.BUY, result.getStatus());
        assertEquals(800.0, user.getBalance());
        verify(transactionRepository).save(any(Transaction.class));
        verify(userRepository).save(user);
    }

    @Test
    void createOutgoingTransaction_Should_Throw_WhenUserNull() {
        Transaction tx = new Transaction();
        tx.setAmount(100.0);

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.createOutgoingTransaction(null, tx));
    }

    @Test
    void findTransactionById_Should_Return_Transaction_WhenExists() {
        Transaction tx = new Transaction();
        tx.setId(1L);
        when(transactionRepository.findTransactionById(1L)).thenReturn(tx);

        Transaction result = transactionService.findTransactionById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void findTransactionById_Should_Throw_WhenNotFound() {
        when(transactionRepository.findTransactionById(99L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class,
                () -> transactionService.findTransactionById(99L));
    }

    @Test
    void deleteAllByUser_Should_Delete_All_UserTransactions() {
        User user = new User();
        user.setEmail("test@example.com");

        Transaction tx1 = new Transaction();
        tx1.setUser(user);

        Transaction tx2 = new Transaction();
        tx2.setUser(user);

        when(transactionRepository.findAll()).thenReturn(List.of(tx1, tx2));

        transactionService.deleteAllByUser(user);

        verify(transactionRepository, times(2)).delete(any(Transaction.class));
    }
}
