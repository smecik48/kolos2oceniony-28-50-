package com.example.kolos;

import java.sql.*;
import java.time.LocalDateTime;

public class DBHandler {
    static String url = "jdbc:sqlite:baza.db";
    public static void createDb(){
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                var meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS entry (token TEXT NOT NULL, x INTEGER NOT NULL, y INTEGER NOT NULL, color TEXT NOT NULL, time TEXT NOT NULL);");
            ResultSet rs = ps.executeQuery();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }
    public static void insertValues(String id, int x, int y, String color){
        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement ps = conn.prepareStatement("INSERT INTO entry (token, x, y, color, timestamp) VALUES(?, ?, ?, ?, ?);");
            ps.setString(1, id);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setString(4, color);
            ps.setString(5, LocalDateTime.now().toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
