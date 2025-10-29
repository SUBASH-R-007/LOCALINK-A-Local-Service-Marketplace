package com.localink.dao;

import com.localink.config.Database;
import com.localink.model.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDao {
    public List<String> listCategories() throws SQLException {
        String sql = "SELECT DISTINCT category FROM services WHERE is_active = TRUE ORDER BY category";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<String> categories = new ArrayList<>();
            while (rs.next()) {
                categories.add(rs.getString(1));
            }
            return categories;
        }
    }

    public List<Service> listByCategory(String category) throws SQLException {
        String sql = "SELECT s.id, s.provider_id, u.name AS provider_name, s.title, s.category, s.description, s.hourly_rate, s.is_active " +
                "FROM services s JOIN users u ON s.provider_id = u.id " +
                "WHERE s.is_active = TRUE AND s.category = ? ORDER BY s.title";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                List<Service> list = new ArrayList<>();
                while (rs.next()) {
                    Service svc = new Service(
                            rs.getLong("id"),
                            rs.getLong("provider_id"),
                            rs.getString("provider_name"),
                            rs.getString("title"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getDouble("hourly_rate"),
                            rs.getBoolean("is_active")
                    );
                    list.add(svc);
                }
                return list;
            }
        }
    }

    public List<Service> listAllActive() throws SQLException {
        String sql = "SELECT s.id, s.provider_id, u.name AS provider_name, s.title, s.category, s.description, s.hourly_rate, s.is_active " +
                "FROM services s JOIN users u ON s.provider_id = u.id " +
                "WHERE s.is_active = TRUE ORDER BY s.title";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Service> list = new ArrayList<>();
            while (rs.next()) {
                Service svc = new Service(
                        rs.getLong("id"),
                        rs.getLong("provider_id"),
                        rs.getString("provider_name"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getDouble("hourly_rate"),
                        rs.getBoolean("is_active")
                );
                list.add(svc);
            }
            return list;
        }
    }

    public List<Service> listByProvider(long providerId) throws SQLException {
        String sql = "SELECT s.id, s.provider_id, u.name AS provider_name, s.title, s.category, s.description, s.hourly_rate, s.is_active " +
                "FROM services s JOIN users u ON s.provider_id = u.id " +
                "WHERE s.provider_id = ? ORDER BY s.title";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, providerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Service> list = new ArrayList<>();
                while (rs.next()) {
                    Service svc = new Service(
                            rs.getLong("id"),
                            rs.getLong("provider_id"),
                            rs.getString("provider_name"),
                            rs.getString("title"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getDouble("hourly_rate"),
                            rs.getBoolean("is_active")
                    );
                    list.add(svc);
                }
                return list;
            }
        }
    }

    public long insert(long providerId, String title, String category, String description, double hourlyRate) throws SQLException {
        String sql = "INSERT INTO services (provider_id, title, category, description, hourly_rate, is_active) VALUES (?,?,?,?,?, TRUE) RETURNING id";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, providerId);
            ps.setString(2, title);
            ps.setString(3, category);
            ps.setString(4, description);
            ps.setDouble(5, hourlyRate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to insert service");
    }
}

