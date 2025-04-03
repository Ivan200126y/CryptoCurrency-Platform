package com.telerikacademy.web.cryptocurrency_trading_platform;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class CryptoPricesFetch {

    private final String API_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=20&page=1";

    public List<String> fetchCryptoPrices(){
        WebClient webClient = WebClient.builder().baseUrl(API_URL).build();
        String response = webClient.get().retrieve().bodyToMono(String.class).block();

        List<String> cryptoList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(response);

            for(int i = 0; i < 20; i++){
                JSONObject coin = jsonArray.getJSONObject(i);
                String name = coin.getString("name");
                String symbol = coin.getString("symbol").toUpperCase();
                Double price = coin.getDouble("current_price");

                cryptoList.add(name + " (" + symbol + ") $" + price);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cryptoList;
    }

}


//https://api.kraken.com/0/public/Ticker