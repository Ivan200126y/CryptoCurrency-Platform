package com.telerikacademy.web.cryptocurrency_trading_platform.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CryptoInfo {

    private String name;
    private String symbol;
    private Double price;
}
