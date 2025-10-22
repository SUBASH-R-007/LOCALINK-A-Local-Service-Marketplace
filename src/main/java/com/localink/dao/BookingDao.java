package com.localink.dao;

import com.localink.config.Database;

import java.sql.*;
import java.time.ZonedDateTime;

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
}
