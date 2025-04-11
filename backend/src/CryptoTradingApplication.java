package com.crypto.platform;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CryptoTradingApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CryptoTradingApplication.class, args);
        try {
            System.out.println("Initializing database connection...");
            
            URI krakenUri = new URI("wss://ws.kraken.com/v2");
            KrakenWebSocketClient client = new KrakenWebSocketClient(krakenUri);
            
            System.out.println("Connecting to Kraken WebSocket API...");
            client.connect();
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down...");
                client.close();
                com.crypto.platform.DatabaseConnectionManager.close();
            }));
            
            System.out.println("Application started");
            
        } catch (URISyntaxException e) {
            System.err.println("Error " + e.getMessage());
        }
    }
}
