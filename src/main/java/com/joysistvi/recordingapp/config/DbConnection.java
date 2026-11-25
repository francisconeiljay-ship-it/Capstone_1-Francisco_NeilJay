package com.joysistvi.recordingapp.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DbConnection {

    private final String url;
    private final String user;
    private final String password;

    public DbConnection() {
        this("jdbc:mysql://localhost:3306/recording_studio_db", "root", "");
    }

    public DbConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
