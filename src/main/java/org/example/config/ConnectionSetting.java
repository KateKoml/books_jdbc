package org.example.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class ConnectionSetting {
    private final String postgresDriver;
    private String dbUrl;
    private final String username;
    private final String password;
    private final String dbName;

    public ConnectionSetting() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            log.error("Failed to load properties file", e);
            throw new RuntimeException("Failed to load properties file");
        }
        this.dbUrl = properties.getProperty("database.url");
        this.username = properties.getProperty("database.login");
        this.password = properties.getProperty("database.password");
        this.postgresDriver = properties.getProperty("driver.name");
        this.dbName = properties.getProperty("database.name");
    }

    public void registerDriver() {
        try {
            Class.forName(getPostgresDriver());
        } catch (ClassNotFoundException e) {
            log.info("JDBC Driver Cannot be loaded!");
            throw new RuntimeException("JDBC Driver Cannot be loaded!");
        }
    }

    public Connection getConnection() {
        String jdbcConnection = getDatabaseUrl();

        try {
            return DriverManager.getConnection(jdbcConnection, getDatabaseUser(), getDatabasePassword());
        } catch (SQLException e) {
            log.info("JDBC connection went wrong:" + e);
            throw new RuntimeException(e);
        }
    }

    private String getPostgresDriver() {
        return postgresDriver;
    }

    private String getDatabaseUrl() {
        return dbUrl;
    }

    private String getDatabaseUser() {
        return username;
    }

    private String getDatabasePassword() {
        return password;
    }

    private String getDbName() {
        return dbName;
    }

    public void setDatabaseUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }
}
