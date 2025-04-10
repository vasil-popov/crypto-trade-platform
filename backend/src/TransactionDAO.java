package com.crypto.platform;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    
    public void recordTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, symbol, quantity, price, total_amount, action) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, transaction.getUserId());
            stmt.setString(2, transaction.getSymbol());
            stmt.setBigDecimal(3, transaction.getQuantity());
            stmt.setBigDecimal(4, transaction.getPrice());
            stmt.setBigDecimal(5, transaction.getTotalAmount());
            stmt.setString(6, transaction.getAction().name());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    transaction.setId(rs.getInt(1));
                }
            }
        }
    }
    
    public List<Transaction> getUserTransactions(int userId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setId(rs.getInt("id"));
                    transaction.setUserId(rs.getInt("user_id"));
                    transaction.setSymbol(rs.getString("symbol"));
                    transaction.setQuantity(rs.getBigDecimal("quantity"));
                    transaction.setPrice(rs.getBigDecimal("price"));
                    transaction.setTotalAmount(rs.getBigDecimal("total_amount"));
                    transaction.setAction(Transaction.TransactionType.valueOf(rs.getString("action")));
                    transaction.setTimestamp(rs.getTimestamp("timestamp"));
                    transactions.add(transaction);
                }
            }
        }
        
        return transactions;
    }


    public void deleteAllTransactions(int userId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}
