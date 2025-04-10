package com.crypto.platform;

public final class DatabaseConfig {
    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/crypto_trading_platform";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "root";
    public static final int MAX_POOL_SIZE = 10;
    public static final int TIMEOUT = 30000;
    
    private DatabaseConfig() {}
}
