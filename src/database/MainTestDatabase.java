package database;

// Đảm bảo import đúng gói chứa lớp DAO của bạn
// ví dụ: import database.SinhVienDAO; 
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;

import static database.KNDatabase.closeConnection;

// Import static nếu bạn để hàm này ở class khác


public class MainTestDatabase {
    // Xóa bỏ <SinhVienDAO> ở đây
    public static void main(String[] args) throws SQLServerException {
        System.out.println("--- Đang kiểm tra kết nối ---");

        // Gọi hàm static trực tiếp từ tên class
        try (Connection conn = KNDatabase.getConnection()) {

            if (conn != null && !conn.isClosed()) {
                System.out.println("KẾT NỐI THÀNH CÔNG!");
                System.out.println("Thông tin Database: " + conn.getMetaData().getDatabaseProductName());
            } else {
                System.out.println("KẾT NỐI THẤT BẠI (Connection bị null hoặc đã đóng).");
            }

        } catch (Exception e) {
            System.out.println(" CÓ LỖI XẢY RA:");
            e.printStackTrace();
        }

    }
}