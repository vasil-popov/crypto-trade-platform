package com.crypto.platform;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class TickerController {

    @GetMapping("/tickers")
    public Collection<JsonNode> getTickers() {
        return TickerService.getAllTickers();
    }
}