package com.crypto.platform;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoldingDAO {
    
    public List<Holding> getUserHoldings(int userId) throws SQLException {
        String sql = "SELECT * FROM holdings WHERE user_id = ?";
        List<Holding> holdings = new ArrayList<>();
        
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Holding holding = new Holding();
                    holding.setId(rs.getInt("id"));
                    holding.setUserId(rs.getInt("user_id"));
                    holding.setSymbol(rs.getString("symbol"));
                    holding.setQuantity(rs.getBigDecimal("quantity"));
                    holdings.add(holding);
                }
            }
        }
        
        return holdings;
    }
    
    public Holding getHolding(int userId, String symbol) throws SQLException {
        String sql = "SELECT * FROM holdings WHERE user_id = ? AND symbol = ?";
        
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, symbol);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Holding holding = new Holding();
                    holding.setId(rs.getInt("id"));
                    holding.setUserId(rs.getInt("user_id"));
                    holding.setSymbol(rs.getString("symbol"));
                    holding.setQuantity(rs.getBigDecimal("quantity"));
                    return holding;
                } else {
                    return null;
                }
            }
        }
    }
    
    public void updateHolding(int userId, String symbol, BigDecimal quantity) throws SQLException {

        Holding existing = getHolding(userId, symbol);
        
        if (existing == null) {
            String insertSql = "INSERT INTO holdings (user_id, symbol, quantity) VALUES (?, ?, ?)";
            
            try (Connection conn = DatabaseConnectionManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                
                stmt.setInt(1, userId);
                stmt.setString(2, symbol);
                stmt.setBigDecimal(3, quantity);
                
                stmt.executeUpdate();
            }
        } else {
            String updateSql = "UPDATE holdings SET quantity = ? WHERE user_id = ? AND symbol = ?";
            
            try (Connection conn = DatabaseConnectionManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                
                stmt.setBigDecimal(1, quantity);
                stmt.setInt(2, userId);
                stmt.setString(3, symbol);
                
                stmt.executeUpdate();
            }
        }
    }
}
