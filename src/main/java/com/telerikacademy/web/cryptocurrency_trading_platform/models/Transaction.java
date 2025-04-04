package com.telerikacademy.web.cryptocurrency_trading_platform.models;

import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "amount")
    private double amount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "currency")
    private String currency;

    @Column(name = "price")
    private Double price;

    @Column(name="shares")
    private Double shares;

    public Transaction() {
    }
}
