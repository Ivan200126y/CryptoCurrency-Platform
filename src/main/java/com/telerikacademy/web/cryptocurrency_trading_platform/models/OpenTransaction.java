package com.telerikacademy.web.cryptocurrency_trading_platform.models;

import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OpenTransaction {

    private Long id;

    private User user;

    private Double amount;

    private Status status;

    private LocalDateTime createdAt;

    private String currency;

    private String price;

    private Double currentPrice;

}
