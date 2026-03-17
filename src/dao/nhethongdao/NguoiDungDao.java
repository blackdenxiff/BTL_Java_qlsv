package dao.nhethongdao;

import database.KNDatabase;
import model.nhethong.NguoiDung;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NguoiDungDao {

    public void insertNguoiDung(NguoiDung nd) {
        String sql = "INSERT INTO NguoiDung (username, matKhau, usertype, maSV, maNV) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("Loi: Khong the ket noi co so du lieu!");
                return;
            }

            pstmt.setString(1, nd.getUsername());
            pstmt.setString(2, nd.getMatKhau());
            pstmt.setString(3, nd.getUsertype());

            // Xu ly truong hop maSV hoac maNV bi null
            pstmt.setString(4, nd.getMaSV());
            pstmt.setString(5, nd.getMaNV());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Them tai khoan " + nd.getUsername() + " thanh cong!");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi them nguoi dung: " + e.getMessage());
        }
    }

    public void updateNguoiDung(NguoiDung nd) {
        // Thuong sua mat khau, loai nguoi dung hoac lien ket MaSV/MaNV
        String sql = "UPDATE NguoiDung SET matKhau = ?, usertype = ?, maSV = ?, maNV = ? WHERE username = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, nd.getMatKhau());
            pstmt.setString(2, nd.getUsertype());
            pstmt.setString(3, nd.getMaSV());
            pstmt.setString(4, nd.getMaNV());

            // Dieu kien WHERE dua tren username
            pstmt.setString(5, nd.getUsername());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cap nhat tai khoan " + nd.getUsername() + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay username " + nd.getUsername());
            }

        } catch (SQLException e) {
            System.out.println("Loi khi cap nhat nguoi dung: " + e.getMessage());
        }
    }

    public void deleteNguoiDung(String username) {
        String sql = "DELETE FROM NguoiDung WHERE username = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, username);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Xoa tai khoan " + username + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay username " + username + " de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa nguoi dung: " + e.getMessage());
        }
    }
}
