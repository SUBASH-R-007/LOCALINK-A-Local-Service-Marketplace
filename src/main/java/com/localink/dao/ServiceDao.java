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
}
