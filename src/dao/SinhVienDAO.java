package dao;

import model.nconnguoi.SinhVien;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

public class SinhVienDAO {
    // Thay đổi thông tin kết nối phù hợp với máy của bạn
    private String url = "jdbc:sqlserver://localhost:1433;databaseName=Ten_CSDL_Cua_Ban;encrypt=true;trustServerCertificate=true;";
    private String user = "sa";
    private String password = "your_password";

    public void insertSinhVien(SinhVien sv) {
        String sql = "INSERT INTO SinhVien (MaSV, HoDem, Ten, NgaySinh, GioiTinh, NamNhapHoc, MaLop, SDT, Email, TrangThaiSV, MaHuyen) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Truyền tham số vào câu lệnh SQL
            pstmt.setString(1, sv.getMaSV());
            pstmt.setString(2, sv.getHoDem());
            pstmt.setString(3, sv.getTen());

            // Chuyển từ java.time.LocalDate sang java.sql.Date
            pstmt.setDate(4, Date.valueOf(sv.getNgaySinh()));

            pstmt.setString(5, sv.getGioiTinh());
            pstmt.setInt(6, sv.getNamNhapHoc());
            pstmt.setString(7, sv.getMaLop());
            pstmt.setString(8, sv.getSdt());
            pstmt.setString(9, sv.getEmail());
            pstmt.setString(10, sv.getTrangThaiSV());
            pstmt.setString(11, sv.getMaHuyen());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Thêm sinh viên thành công!");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi chèn dữ liệu: " + e.getMessage());
        }
    }
}
