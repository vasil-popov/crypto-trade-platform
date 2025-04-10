package com.crypto.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class TickerService {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final Map<String, TickerData> tickerMap = new HashMap<>();
    
    public static class TickerData {
        private String symbol;
        private BigDecimal lastPrice;
        private BigDecimal high;
        private BigDecimal low;
        private BigDecimal volume24h;
        private BigDecimal priceChange24h;
        private BigDecimal volumeUsd; 
        
        public TickerData(String symbol) {
            this.symbol = symbol;
        }
        
        public String getSymbol() {
            return symbol;
        }
        
        public BigDecimal getLastPrice() {
            return lastPrice;
        }
        
        public void setLastPrice(BigDecimal lastPrice) {
            this.lastPrice = lastPrice;
        }
        
        public BigDecimal getHigh() {
            return high;
        }
        
        public void setHigh(BigDecimal high) {
            this.high = high;
        }
        
        public BigDecimal getLow() {
            return low;
        }
        
        public void setLow(BigDecimal low) {
            this.low = low;
        }
        
        public BigDecimal getVolume24h() {
            return volume24h;
        }
        
        public void setVolume24h(BigDecimal volume24h) {
            this.volume24h = volume24h;
        }
        
        public BigDecimal getPriceChange24h() {
            return priceChange24h;
        }
        
        public void setPriceChange24h(BigDecimal priceChange24h) {
            this.priceChange24h = priceChange24h;
        }
        
        public BigDecimal getVolumeUsd() {
            return volumeUsd;
        }
        
        public void setVolumeUsd(BigDecimal volumeUsd) {
            this.volumeUsd = volumeUsd;
        }
        
        public BigDecimal calculateVolumeUsd() {
            if (volume24h == null || lastPrice == null) {
                return BigDecimal.ZERO;
            }
            this.volumeUsd = volume24h.multiply(lastPrice).setScale(2, RoundingMode.HALF_UP);
            
            return this.volumeUsd;
        }
    }
    
    public static synchronized void updateTicker(String symbol, JsonNode tickerData) {
        TickerData data = tickerMap.computeIfAbsent(symbol, TickerData::new);
        
        if (tickerData.has("last")) {
            data.setLastPrice(new BigDecimal(tickerData.get("last").asText()));
        }
        
        if (tickerData.has("high")) {
            data.setHigh(new BigDecimal(tickerData.get("high").asText()));
        }

        if (tickerData.has("low")) {
            data.setLow(new BigDecimal(tickerData.get("low").asText()));
        }
        
        if (tickerData.has("volume")) {
            data.setVolume24h(new BigDecimal(tickerData.get("volume").asText()));
        }
        
        if (tickerData.has("change_pct")) {
            data.setPriceChange24h(new BigDecimal(tickerData.get("change_pct").asText()));
        }
        
        BigDecimal volumeUsd = data.calculateVolumeUsd();
        data.setVolumeUsd(volumeUsd);
        
        notifyTickerChange(symbol, data);
    }
    
    public static TickerData getTickerData(String symbol) {
        return tickerMap.get(symbol);
    }
    
    public static Map<String, TickerData> getAllTickers() {
        return new HashMap<>(tickerMap);
    }
    
    private static void notifyTickerChange(String symbol, TickerData data) {
        System.out.println("Ticker updated: " + symbol + " Price: " + data.getLastPrice());
    }
}