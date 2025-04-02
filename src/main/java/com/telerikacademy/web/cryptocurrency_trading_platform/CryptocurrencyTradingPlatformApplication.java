package com.telerikacademy.web.cryptocurrency_trading_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EntityScan(basePackages = "com.telerikacademy.web.cryptocurrency_trading_platform.models")
public class CryptocurrencyTradingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptocurrencyTradingPlatformApplication.class, args);
    }

    //db - transactions, user
}
