package com.crypto.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class KrakenWebSocketClient extends WebSocketClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public KrakenWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to Kraken WebSocket API");

        String subscriptionMessage = """
        {
            "method": "subscribe",
            "params": {
                "channel": "ticker",
                "symbol": ["BTC/USD", "ETH/USD", "XRP/USD", "SOL/USD", "DOGE/USD", "TRX/USD", "ADA/USD", "WBTC/USD", "TON/USD", "LINK/USD", "XLM/USD", "SHIB/USD", "AVAX/USD", "SUI/USD", "DOT/USD", "OM/USD", "BCH/USD", "LTC/USD", "XMR/USD", "UNI/USD"]
            }
        }
        """;
        send(subscriptionMessage);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Raw message received: " + message);
        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            if (jsonNode.has("channel") && "ticker".equals(jsonNode.get("channel").asText())) {
                JsonNode data = jsonNode.get("data");

                if (data != null && data.isArray()) {
                    for (JsonNode ticker : data) {
                        String symbol = ticker.get("symbol").asText();
                        TickerService.updateTicker(symbol, ticker);
                    }
                }
            } else if (jsonNode.has("channel") && "heartbeat".equals(jsonNode.get("channel").asText())) {
                System.out.println("Heartbeat received.");
            } else if (jsonNode.has("event") && "subscriptionStatus".equals(jsonNode.get("event").asText())) {
                System.out.println("Subscription status: " + message);
                if (jsonNode.has("status") && "error".equals(jsonNode.get("status").asText())) {
                    System.out.println("Subscription error: " + jsonNode.get("errorMessage").asText());
                }
            } else {
                System.out.println("Unhandled message: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}