package dao.nhethongdao;

import database.KNDatabase;
import model.nhethong.NguoiDung;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ThangDiemDAO {

    public void insertNguoiDung(NguoiDung nd) {
        String sql = "INSERT INTO NguoiDung (username, matKhau, usertype, maSV, maNV) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, nd.getUsername());
            pstmt.setString(2, nd.getMatKhau());
            pstmt.setString(3, nd.getUsertype());

            // Xu ly maSV (Neu null thi setNull trong SQL)
            if (nd.getMaSV() != null && !nd.getMaSV().isEmpty()) {
                pstmt.setString(4, nd.getMaSV());
            } else {
                pstmt.setNull(4, Types.VARCHAR);
            }

            // Xu ly maNV (Neu null thi setNull trong SQL)
            if (nd.getMaNV() != null && !nd.getMaNV().isEmpty()) {
                pstmt.setString(5, nd.getMaNV());
            } else {
                pstmt.setNull(5, Types.VARCHAR);
            }

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Them tai khoan " + nd.getUsername() + " thanh cong!");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi them nguoi dung: " + e.getMessage());
        }
    }

    public void updateNguoiDung(NguoiDung nd) {
        String sql = "UPDATE NguoiDung SET matKhau = ?, usertype = ?, maSV = ?, maNV = ? WHERE username = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, nd.getMatKhau());
            pstmt.setString(2, nd.getUsertype());

            // Xu ly maSV nullable
            if (nd.getMaSV() != null && !nd.getMaSV().isEmpty()) {
                pstmt.setString(3, nd.getMaSV());
            } else {
                pstmt.setNull(3, Types.VARCHAR);
            }

            // Xu ly maNV nullable
            if (nd.getMaNV() != null && !nd.getMaNV().isEmpty()) {
                pstmt.setString(4, nd.getMaNV());
            } else {
                pstmt.setNull(4, Types.VARCHAR);
            }

            pstmt.setString(5, nd.getUsername());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Cap nhat tai khoan " + nd.getUsername() + " thanh cong!");
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

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Xoa tai khoan " + username + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay username de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa nguoi dung: " + e.getMessage());
        }
    }

}
