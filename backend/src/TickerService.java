package com.crypto.platform;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class TickerService {

    private static final ConcurrentHashMap<String, JsonNode> latestTickers = new ConcurrentHashMap<>();

    public static void updateTicker(String symbol, JsonNode tickerData) {
        latestTickers.put(symbol, tickerData);
    }

    public static Collection<JsonNode> getAllTickers() {
        return latestTickers.values();
    }
}