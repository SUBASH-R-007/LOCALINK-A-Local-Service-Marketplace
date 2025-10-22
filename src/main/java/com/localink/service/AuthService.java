package com.localink.service;

import com.localink.dao.UserDao;
import com.localink.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public User login(String email, String password) throws SQLException {
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Email and password are required");
        }
        User user = userDao.findByEmail(email.trim());
        if (user == null) return null;
        String hash = user.getPasswordHash();
        if (hash != null && BCrypt.checkpw(password, hash)) {
            return user;
        }
        return null;
    }

    public long register(String name, String email, String password, String role) throws SQLException {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");
        if (password == null || password.length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters");
        if (role == null || role.isBlank()) role = "CUSTOMER";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
        return userDao.insert(name.trim(), email.trim().toLowerCase(), hash, role);
    }
}
