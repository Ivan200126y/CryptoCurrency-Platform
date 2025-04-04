package com.telerikacademy.web.cryptocurrency_trading_platform;

import com.telerikacademy.web.cryptocurrency_trading_platform.models.CryptoInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CryptoPricesFetch {

    private final String API_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=20&page=1";
    //https://docs.kraken.com/api/docs/websocket-v2/ticker
//    https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=20&page=1
    private List<CryptoInfo> cachedPrices = new ArrayList<>();
    private long lastFetchedTime = 0;
    private static final long CACHE_DURATION_MS = 60000;

    public List<CryptoInfo> fetchCryptoPrices() {
        if (!cachedPrices.isEmpty() && (System.currentTimeMillis() - lastFetchedTime < CACHE_DURATION_MS)) {
            return cachedPrices;
        }

        try {
            WebClient webClient = WebClient.builder().baseUrl(API_URL).build();
            String response = webClient.get().retrieve().bodyToMono(String.class).block();

            JSONArray jsonArray = new JSONArray(response);
            List<CryptoInfo> cryptoList = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                JSONObject coin = jsonArray.getJSONObject(i);
                String name = coin.getString("name");
                String symbol = coin.getString("symbol").toUpperCase();
                double price = coin.getDouble("current_price");

                cryptoList.add(new CryptoInfo(name, symbol, price));
            }

            cachedPrices = cryptoList;
            lastFetchedTime = System.currentTimeMillis();
            return cryptoList;

        } catch (Exception e) {
            e.printStackTrace();
            return cachedPrices;
        }
    }


    public Optional<String> getPriceForSymbol(String symbol) {
        return fetchCryptoPrices().stream()
                .filter(c -> c.getSymbol().equalsIgnoreCase(symbol))
                .map(c -> c.getPrice().toString())
                .findFirst();
    }


}


//https://api.kraken.com/0/public/Ticker