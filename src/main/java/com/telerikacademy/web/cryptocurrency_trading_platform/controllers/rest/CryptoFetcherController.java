package com.telerikacademy.web.cryptocurrency_trading_platform.controllers.rest;

import com.telerikacademy.web.cryptocurrency_trading_platform.CryptoPricesFetch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/crypto")
public class CryptoFetcherController {

    CryptoPricesFetch cryptoPricesFetch;

    public CryptoFetcherController(CryptoPricesFetch cryptoPricesFetch) {
        this.cryptoPricesFetch = cryptoPricesFetch;
    }

    @GetMapping
    public List<String> getLatestPrices() {
        return cryptoPricesFetch.fetchCryptoPrices();
    }
}
