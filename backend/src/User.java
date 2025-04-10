package com.crypto.platform;

import java.math.BigDecimal;

public class User {
    private int id;
    private BigDecimal balance;
    private BigDecimal initialBalance;
    
    public User() {}
    
    public User(int id, BigDecimal balance, BigDecimal initialBalance) {
        this.id = id;
        this.balance = balance;
        this.initialBalance = initialBalance;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public BigDecimal getInitialBalance() {
        return initialBalance;
    }
    
    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
