package com.meeting.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/meeting_management";
    private static final String USER = "root";
    private static final String PASSWORD = "QAyeuHutaodenchet02092006";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load driver (Tuỳ chọn cho các bản JDBC mới, nhưng nên giữ cho chắc chắn)
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Lỗi kết nối Cơ sở dữ liệu: " + e.getMessage());
        }
        return conn;
    }
}