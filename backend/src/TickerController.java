package com.crypto.platform;

import com.crypto.platform.TickerService.TickerData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class TickerController {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    

    @GetMapping("/tickers")
    public JsonNode getTickers() {
        return convertAllTickersToJson();
    }
    

    
    private ArrayNode convertAllTickersToJson() {
        ArrayNode responseArray = objectMapper.createArrayNode();
        
        for (TickerData data : TickerService.getAllTickers().values()) {
            ObjectNode tickerNode = createTickerJsonNode(data);
            responseArray.add(tickerNode);
        }
        
        return responseArray;
    }
    
    
    private ObjectNode createTickerJsonNode(TickerData data) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("symbol", data.getSymbol());
        
        if (data.getLastPrice() != null) {
            node.put("price", data.getLastPrice().doubleValue());
        }
        
        if (data.getHigh() != null) {
            node.put("high", data.getHigh().doubleValue());
        }
        
        if (data.getLow() != null) {
            node.put("low", data.getLow().doubleValue());
        }
        
        if (data.getVolume24h() != null && data.getLastPrice() != null) {
            BigDecimal volumeUsd = data.calculateVolumeUsd();
            node.put("volume", volumeUsd.toPlainString());
        }
        
        if (data.getPriceChange24h() != null) {
            node.put("change", data.getPriceChange24h().doubleValue());
        }
        
        return node;
    }
}