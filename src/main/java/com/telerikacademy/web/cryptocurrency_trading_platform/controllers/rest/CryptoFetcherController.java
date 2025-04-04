package com.telerikacademy.web.cryptocurrency_trading_platform.controllers.rest;

import com.telerikacademy.web.cryptocurrency_trading_platform.CryptoPricesFetch;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.CryptoInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public List<CryptoInfo> getLatestPrices() {
        return cryptoPricesFetch.fetchCryptoPrices();
    }

    @GetMapping("/price")
    public ResponseEntity<Double> getPrice(@RequestParam("symbol") String symbol) {
        return cryptoPricesFetch.getPriceForSymbol(symbol)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body(0.0));
    }

}

