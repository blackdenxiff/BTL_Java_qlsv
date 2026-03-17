package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KNDatabase {
    // Thông tin tập trung tại một nơi duy nhất
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=SQLqlsv2;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASS = "123456789";

    // Hàm lấy kết nối
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
        }
        return conn;
    }

    // Hàm đóng kết nối để giải phóng tài nguyên
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}