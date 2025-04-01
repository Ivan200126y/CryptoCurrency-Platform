package com.telerikacademy.web.cryptocurrency_trading_platform.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionDTO {

    private int user_id;
    private double amount;
    private Status status;
    private LocalDateTime createdAt;
}
