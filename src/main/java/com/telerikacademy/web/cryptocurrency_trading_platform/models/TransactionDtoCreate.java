package com.telerikacademy.web.cryptocurrency_trading_platform.models;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TransactionDtoCreate {

    @Pattern(regexp = "^(?!0(\\.0+)?$)\\d+(\\.\\d{1,2})?$", message = "Amount must be a positive number above 0!")
    private String amount;

    private String currency;

    private String type;

    private Double priceAtPurchase;

    private double shares;
}
