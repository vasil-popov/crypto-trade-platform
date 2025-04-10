package com.crypto.platform;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private static volatile HikariDataSource dataSource;
    
    public static Connection getConnection() throws SQLException {
        HikariDataSource ds = dataSource;
        if (ds == null) {
            synchronized (DatabaseConnectionManager.class) {
                ds = dataSource;
                if (ds == null) {
                    dataSource = ds = initializeDataSource();
                }
            }
        }
        return ds.getConnection();
    }
    
    private static HikariDataSource initializeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DatabaseConfig.JDBC_URL);
        config.setUsername(DatabaseConfig.USERNAME);
        config.setPassword(DatabaseConfig.PASSWORD);
        config.setMaximumPoolSize(DatabaseConfig.MAX_POOL_SIZE);
        config.setConnectionTimeout(DatabaseConfig.TIMEOUT);
        
        return new HikariDataSource(config);
    }
    
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
