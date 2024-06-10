package ru.andreycherenkov.db.impl;

import ru.andreycherenkov.db.ConnectionManager;


import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class MySQLConnectionManager implements ConnectionManager {

    Properties properties = new Properties();
    public MySQLConnectionManager() {
        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/db.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла свойств", e);
        }
    }



    @Override
    public Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.mysql.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        return DriverManager.getConnection(url, username, password);
    }
}
