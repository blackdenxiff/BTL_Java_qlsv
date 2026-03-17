package dao.quanlydao;

import database.KNDatabase;
import model.quanly.DangKi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DangKiDAO {

    // 1. Ham Them Dang Ky (Insert)
    public void insertDangKy(DangKi dk) {
        String sql = "INSERT INTO DangKy (maSV, maLHP, diemSo, trangThai) VALUES (?, ?, ?, ?)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, dk.getMaSV());
            pstmt.setString(2, dk.getMaLHP());
            pstmt.setDouble(3, dk.getDiemSo());
            pstmt.setString(4, dk.getTrangThai());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Them dang ky cho sinh vien " + dk.getMaSV() + " thanh cong!");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi them dang ky: " + e.getMessage());
        }
    }

    // Thuong dung de cap nhat diem so hoac trang thai hoc tap
    public void updateDangKy(DangKi dk) {
        String sql = "UPDATE DangKy SET diemSo = ?, trangThai = ? WHERE maSV = ? AND maLHP = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setDouble(1, dk.getDiemSo());
            pstmt.setString(2, dk.getTrangThai());

            // Dieu kien WHERE (Dung ca 2 khoa de xac dinh dung ban ghi)
            pstmt.setString(3, dk.getMaSV());
            pstmt.setString(4, dk.getMaLHP());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Cap nhat diem/trang thai cho sinh vien " + dk.getMaSV() + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay thong tin dang ky de cap nhat.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi cap nhat dang ky: " + e.getMessage());
        }
    }

    public void deleteDangKy(String maSV, String maLHP) {
        String sql = "DELETE FROM DangKy WHERE maSV = ? AND maLHP = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, maSV);
            pstmt.setString(2, maLHP);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Xoa dang ky mon hoc cua sinh vien " + maSV + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay thong tin dang ky de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa dang ky: " + e.getMessage());
        }
    }
}