package com.crypto.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final int DEFAULT_USER_ID = 1;
    
    private final UserDAO userDAO = new UserDAO();
    private final HoldingDAO holdingDAO = new HoldingDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    
    private void initializeUser() {
        try {
            User user = userDAO.getUserById(DEFAULT_USER_ID);
            if (user == null) {
                userDAO.createUser();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing user", e);
        }
    }
    
    @GetMapping("/user")
    public JsonNode getUserData() {
        initializeUser();
        
        ObjectNode userNode = objectMapper.createObjectNode();
        
        try {
            User user = userDAO.getUserById(DEFAULT_USER_ID);
            userNode.put("balance", user.getBalance());
            
            List<Holding> userHoldings = holdingDAO.getUserHoldings(DEFAULT_USER_ID);
            ObjectNode holdingsNode = objectMapper.createObjectNode();
            
            for (Holding holding : userHoldings) {
                if (holding.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    holdingsNode.put(holding.getSymbol(), holding.getQuantity().doubleValue());
                }
            }
            
            userNode.set("holdings", holdingsNode);
            
        } catch (SQLException e) {
            e.printStackTrace();
            userNode.put("error", "Database error: " + e.getMessage());
        }
        
        return userNode;
    }
    
    @GetMapping("/transactions")
    public JsonNode getTransactions() {
        initializeUser();
        
        try {
            List<Transaction> dbTransactions = transactionDAO.getUserTransactions(DEFAULT_USER_ID);
            List<Map<String, Object>> formattedTransactions = new ArrayList<>();
            
            for (Transaction tx : dbTransactions) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("timestamp", tx.getTimestamp().getTime());
                transaction.put("action", tx.getAction().toString());
                transaction.put("symbol", tx.getSymbol());
                transaction.put("quantity", tx.getQuantity().doubleValue());
                transaction.put("price", tx.getPrice().doubleValue());
                
                if (tx.getAction() == Transaction.TransactionType.SELL) {
                    double avgBuyPrice = getAverageBuyPrice(tx.getSymbol(), tx.getTimestamp().getTime());
                    double profitLoss = (tx.getPrice().doubleValue() - avgBuyPrice) * tx.getQuantity().doubleValue();
                    transaction.put("profitLoss", profitLoss);
                } else {
                    transaction.put("profitLoss", null);
                }
                
                formattedTransactions.add(0, transaction); 
            }
            
            return objectMapper.valueToTree(formattedTransactions);
            
        } catch (SQLException e) {
            e.printStackTrace();
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("error", "Database error: " + e.getMessage());
            return errorNode;
        }
    }
    
    @PostMapping("/buy")
    public JsonNode buyAsset(@RequestBody JsonNode request) {
        initializeUser();
        
        String symbol = request.get("symbol").asText();
        BigDecimal quantity = new BigDecimal(request.get("quantity").asText());
        BigDecimal price = request.has("price") ? 
                        new BigDecimal(request.get("price").asText()) : 
                        new BigDecimal(getTickerPrice(symbol));
        
        BigDecimal totalCost = price.multiply(quantity);
        
        ObjectNode response = objectMapper.createObjectNode();
        
        try {
            User user = userDAO.getUserById(DEFAULT_USER_ID);
            
            if (user.getBalance().compareTo(totalCost) < 0) {
                response.put("success", false);
                response.put("message", "Insufficient funds");
                return response;
            }
            
            BigDecimal newBalance = user.getBalance().subtract(totalCost);
            userDAO.updateUserBalance(DEFAULT_USER_ID, newBalance);
            
            Holding currentHolding = holdingDAO.getHolding(DEFAULT_USER_ID, symbol);
            BigDecimal newQuantity = currentHolding != null ? 
                    currentHolding.getQuantity().add(quantity) : 
                    quantity;
            holdingDAO.updateHolding(DEFAULT_USER_ID, symbol, newQuantity);
            
            Transaction transaction = new Transaction(
                DEFAULT_USER_ID, 
                symbol, 
                quantity, 
                price, 
                totalCost, 
                Transaction.TransactionType.BUY
            );
            transactionDAO.recordTransaction(transaction);
            
            response.put("success", true);
            response.put("balance", newBalance.doubleValue());
            response.put("holding", newQuantity.doubleValue());
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Database error: " + e.getMessage());
        }
        
        return response;
    }
    
    @PostMapping("/sell")
    public JsonNode sellAsset(@RequestBody JsonNode request) {
        initializeUser();
        
        String symbol = request.get("symbol").asText();
        BigDecimal quantity = new BigDecimal(request.get("quantity").asText());
        BigDecimal price = request.has("price") ? 
                        new BigDecimal(request.get("price").asText()) : 
                        new BigDecimal(getTickerPrice(symbol));
        
        BigDecimal totalValue = price.multiply(quantity);
        
        ObjectNode response = objectMapper.createObjectNode();
        
        try {
            User user = userDAO.getUserById(DEFAULT_USER_ID);
            
            Holding currentHolding = holdingDAO.getHolding(DEFAULT_USER_ID, symbol);
            if (currentHolding == null || currentHolding.getQuantity().compareTo(quantity) < 0) {
                response.put("success", false);
                response.put("message", "Insufficient holdings");
                return response;
            }
            
            double profitLoss = calculateProfitLoss(symbol, quantity, price);
            
            BigDecimal newBalance = user.getBalance().add(totalValue);
            userDAO.updateUserBalance(DEFAULT_USER_ID, newBalance);
            
            BigDecimal newQuantity = currentHolding.getQuantity().subtract(quantity);
            holdingDAO.updateHolding(DEFAULT_USER_ID, symbol, newQuantity);
            
            Transaction transaction = new Transaction(
                DEFAULT_USER_ID, 
                symbol, 
                quantity, 
                price, 
                totalValue, 
                Transaction.TransactionType.SELL
            );
            transactionDAO.recordTransaction(transaction);
            
            response.put("success", true);
            response.put("balance", newBalance.doubleValue());
            response.put("holding", newQuantity.doubleValue());
            response.put("profitLoss", profitLoss);
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Database error: " + e.getMessage());
        }
        
        return response;
    }
    
    @PostMapping("/reset")
    public JsonNode resetAccount() {
        ObjectNode response = objectMapper.createObjectNode();
        
        try {
            User user = userDAO.getUserById(DEFAULT_USER_ID);
            if (user == null) {
                userDAO.createUser();
            } else {
                userDAO.updateUserBalance(DEFAULT_USER_ID, new BigDecimal("10000.00"));
            }
            
            List<Holding> holdings = holdingDAO.getUserHoldings(DEFAULT_USER_ID);
            for (Holding holding : holdings) {
                holdingDAO.updateHolding(DEFAULT_USER_ID, holding.getSymbol(), BigDecimal.ZERO);
            }
            
            transactionDAO.deleteAllTransactions(DEFAULT_USER_ID);
            
            response.put("success", true);
            response.put("message", "Account reset to $10,000");
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Database error: " + e.getMessage());
        }
        
        return response;
    }
    
    private double getTickerPrice(String symbol) {
        TickerService.TickerData ticker = TickerService.getTickerData(symbol);
        return ticker != null && ticker.getLastPrice() != null ? 
                ticker.getLastPrice().doubleValue() : 0.0;
    }
    
    private double getAverageBuyPrice(String symbol, Long beforeTimestamp) {
        try {
            List<Transaction> transactions = transactionDAO.getUserTransactions(DEFAULT_USER_ID);
            
            double totalCost = 0.0;
            double totalQuantity = 0.0;
            
            for (Transaction transaction : transactions) {
                if (transaction.getSymbol().equals(symbol) && 
                    transaction.getAction() == Transaction.TransactionType.BUY &&
                    (beforeTimestamp == null || transaction.getTimestamp().getTime() < beforeTimestamp)) {
                    double price = transaction.getPrice().doubleValue();
                    double quantity = transaction.getQuantity().doubleValue();
                    totalCost += price * quantity;
                    totalQuantity += quantity;
                }
            }
            
            return totalQuantity > 0 ? totalCost / totalQuantity : 0.0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    

    private double calculateProfitLoss(String symbol, BigDecimal sellQuantity, BigDecimal sellPrice) {
        try {
            List<Transaction> transactions = transactionDAO.getUserTransactions(DEFAULT_USER_ID);
            
            transactions.sort((t1, t2) -> t1.getTimestamp().compareTo(t2.getTimestamp()));
            
            List<Transaction> buyQueue = new ArrayList<>();
            for (Transaction tx : transactions) {
                if (tx.getSymbol().equals(symbol) && tx.getAction() == Transaction.TransactionType.BUY) {
                    buyQueue.add(tx);
                }
            }
            
            double remainingToSell = sellQuantity.doubleValue();
            double totalProfitLoss = 0.0;
            
            for (Transaction buyTx : buyQueue) {
                if (remainingToSell <= 0) break;
                
                double buyQty = buyTx.getQuantity().doubleValue();
                double buyPrice = buyTx.getPrice().doubleValue();
                
                double sellFromThisBuy = Math.min(buyQty, remainingToSell);
                
                double profitLoss = sellFromThisBuy * (sellPrice.doubleValue() - buyPrice);
                totalProfitLoss += profitLoss;
                
                remainingToSell -= sellFromThisBuy;
            }
            
            return totalProfitLoss;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}