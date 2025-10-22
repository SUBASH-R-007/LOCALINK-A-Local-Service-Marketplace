package com.localink.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private static HikariDataSource dataSource;

    static {
        try (InputStream in = Database.class.getResourceAsStream("/application.properties")) {
            if (in == null) throw new RuntimeException("application.properties not found");
            Properties props = new Properties();
            props.load(in);

            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(props.getProperty("db.url"));
            cfg.setUsername(props.getProperty("db.username"));
            cfg.setPassword(props.getProperty("db.password"));
            cfg.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.max", "10")));
            cfg.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.min", "2")));
            cfg.setDriverClassName("org.postgresql.Driver");
            cfg.addDataSourceProperty("cachePrepStmts", "true");
            cfg.addDataSourceProperty("prepStmtCacheSize", "250");
            cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(cfg);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DB config", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
