package com.localink.dao;

import com.localink.config.Database;

import java.sql.*;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import com.localink.model.Booking;

public class BookingDao {
    public long create(long customerId, long serviceId, ZonedDateTime scheduledAt) throws SQLException {
        String sql = "INSERT INTO bookings (customer_id, service_id, status, scheduled_at) VALUES (?,?, 'PENDING', ?) RETURNING id";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            ps.setLong(2, serviceId);
            ps.setTimestamp(3, Timestamp.from(scheduledAt.toInstant()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to create booking");
    }

    public List<Booking> listAll() throws SQLException {
        String sql = "SELECT id, customer_id, service_id, status, scheduled_at FROM bookings ORDER BY id";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Booking> list = new ArrayList<>();
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getLong("id"));
                b.setCustomerId(rs.getLong("customer_id"));
                b.setServiceId(rs.getLong("service_id"));
                b.setStatus(rs.getString("status"));
                Timestamp ts = rs.getTimestamp("scheduled_at");
                if (ts != null) b.setScheduledAt(ts.toInstant().atZone(ZoneId.systemDefault()));
                list.add(b);
            }
            return list;
        }
    }

    public List<Booking> listByProvider(long providerId) throws SQLException {
        String sql = "SELECT b.id, b.customer_id, b.service_id, b.status, b.scheduled_at " +
                "FROM bookings b JOIN services s ON b.service_id = s.id " +
                "WHERE s.provider_id = ? ORDER BY b.id DESC";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, providerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Booking> list = new ArrayList<>();
                while (rs.next()) {
                    Booking b = new Booking();
                    b.setId(rs.getLong("id"));
                    b.setCustomerId(rs.getLong("customer_id"));
                    b.setServiceId(rs.getLong("service_id"));
                    b.setStatus(rs.getString("status"));
                    Timestamp ts = rs.getTimestamp("scheduled_at");
                    if (ts != null) b.setScheduledAt(ts.toInstant().atZone(ZoneId.systemDefault()));
                    list.add(b);
                }
                return list;
            }
        }
    }
}
