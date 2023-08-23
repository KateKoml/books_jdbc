package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class ConnectionSetting {
    private final Properties properties;

    public ConnectionSetting() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException e) {
            log.error("Failed to load properties file", e);
            throw new RuntimeException("Failed to load properties file");
        }
    }
    public void registerDriver() {
        try {
            Class.forName(properties.getProperty("driver.name"));
        } catch (ClassNotFoundException e) {
            log.info("JDBC Driver Cannot be loaded!");
            throw new RuntimeException("JDBC Driver Cannot be loaded!");
        }
    }

    public Connection getConnection() {
        String jdbcConnection = StringUtils.join(
                properties.getProperty("database.url"),
                properties.getProperty("database.login"),
                properties.getProperty("database.password"));

        try {
            return DriverManager.getConnection(jdbcConnection);
        } catch (SQLException e) {
            log.info("JDBC connection went wrong:" + e);
            throw new RuntimeException(e);
        }
    }
}
