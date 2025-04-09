package com.crypto.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

        try {
            URI krakenUri = new URI("wss://ws.kraken.com/v2");
            KrakenWebSocketClient client = new KrakenWebSocketClient(krakenUri);
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}