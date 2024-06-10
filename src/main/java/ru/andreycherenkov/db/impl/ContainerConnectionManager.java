package ru.andreycherenkov.db.impl;

import ru.andreycherenkov.db.ConnectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ContainerConnectionManager implements ConnectionManager {

    private final String url;
    private final String username;
    private final String password;

    public ContainerConnectionManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
