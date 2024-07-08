package ru.andreycherenkov.db.impl;

import ru.andreycherenkov.db.ConnectionManager;


import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class MySQLConnectionManager implements ConnectionManager {

    private static final String DEFAULT_RESOURCE_PATH = "src/main/resources/db.properties";
    private static final String DB_URL = "db.mysql.url";
    private static final String DB_USER = "db.username";
    private static final String DB_PASSWORD = "db.password";

    private final Properties properties = new Properties();

    public MySQLConnectionManager() {
        try (FileInputStream fileInputStream = new FileInputStream(DEFAULT_RESOURCE_PATH)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла свойств", e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        String url = properties.getProperty(DB_URL);
        String username = properties.getProperty(DB_USER);
        String password = properties.getProperty(DB_PASSWORD);
        return DriverManager.getConnection(url, username, password);
    }
}
