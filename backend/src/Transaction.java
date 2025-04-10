package com.crypto.platform;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private int id;
    private int userId;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private TransactionType action;
    private Timestamp timestamp;
    
    public enum TransactionType {
        BUY, SELL
    }
    
    public Transaction() {}
    
    public Transaction(int userId, String symbol, BigDecimal quantity, 
                      BigDecimal price, BigDecimal totalAmount, 
                      TransactionType action) {
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.action = action;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public TransactionType getAction() {
        return action;
    }
    
    public void setAction(TransactionType action) {
        this.action = action;
    }
    
    public Timestamp getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
