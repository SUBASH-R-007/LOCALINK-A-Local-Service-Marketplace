package com.localink.dao;

import com.localink.config.Database;
import com.localink.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, email, password_hash, role, created_at FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getLong("id"));
                    u.setName(rs.getString("name"));
                    u.setEmail(rs.getString("email"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setRole(rs.getString("role"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) u.setCreatedAt(ts.toInstant().atOffset(java.time.ZoneOffset.UTC));
                    return u;
                }
                return null;
            }
        }
    }

    public long insert(String name, String email, String passwordHash, String role) throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?,?,?,?) RETURNING id";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setString(4, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Failed to insert user");
    }

    public List<User> listAll() throws SQLException {
        String sql = "SELECT id, name, email, password_hash, role, created_at FROM users ORDER BY id";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<User> list = new ArrayList<>();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setRole(rs.getString("role"));
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) u.setCreatedAt(ts.toInstant().atOffset(java.time.ZoneOffset.UTC));
                list.add(u);
            }
            return list;
        }
    }

    public void updatePasswordHash(long userId, String newHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }
}

