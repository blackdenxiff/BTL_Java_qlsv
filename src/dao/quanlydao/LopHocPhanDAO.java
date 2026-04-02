package dao.quanlydao;

import database.KNDatabase;
import model.quanly.LopHocPhan;
import java.sql.ResultSet;// thêm thư viện
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LopHocPhanDAO {

    // 1. Ham Them Lop Hoc Phan (Insert)
    public void insertLHP(LopHocPhan lhp) {
        String sql = "INSERT INTO LopHocPhan (maLHP, namHoc, hocKy, gioiHanSV, maMH, maNV, caHoc, phongHoc) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, lhp.getMaLHP());
            pstmt.setInt(2, lhp.getNamHoc());
            pstmt.setInt(3, lhp.getHocKy());
            pstmt.setInt(4, lhp.getGioiHanSV());
            pstmt.setString(5, lhp.getMaMH());
            pstmt.setString(6, lhp.getMaNV());
            pstmt.setString(7, lhp.getCaHoc());
            pstmt.setString(8, lhp.getPhongHoc());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Them lop hoc phan " + lhp.getMaLHP() + " thanh cong!");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi them lop hoc phan: " + e.getMessage());
        }
    }

    // 2. Ham Sua Lop Hoc Phan (Update)
    public void updateLHP(LopHocPhan lhp) {
        String sql = "UPDATE LopHocPhan SET namHoc = ?, hocKy = ?, gioiHanSV = ?, maMH = ?, "
                + "maNV = ?, caHoc = ?, phongHoc = ? WHERE maLHP = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setInt(1, lhp.getNamHoc());
            pstmt.setInt(2, lhp.getHocKy());
            pstmt.setInt(3, lhp.getGioiHanSV());
            pstmt.setString(4, lhp.getMaMH());
            pstmt.setString(5, lhp.getMaNV());
            pstmt.setString(6, lhp.getCaHoc());
            pstmt.setString(7, lhp.getPhongHoc());

            // Dieu kien WHERE
            pstmt.setString(8, lhp.getMaLHP());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cap nhat lop hoc phan " + lhp.getMaLHP() + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay ma lop hoc phan de cap nhat.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi cap nhat lop hoc phan: " + e.getMessage());
        }
    }

    // 3. Ham Xoa Lop Hoc Phan (Delete)
    public void deleteLHP(String maLHP) {
        String sql = "DELETE FROM LopHocPhan WHERE maLHP = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, maLHP);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Xoa lop hoc phan " + maLHP + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay ma lop hoc phan de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa lop hoc phan: " + e.getMessage());
            System.out.println("Luu y: Kiem tra xem co sinh vien nao da dang ky lop nay hay chua.");
        }
    }
    // --- BỔ SUNG: KIỂM TRA TRÙNG PHÒNG HỌC ---
    public boolean checkTrungPhong(String caHoc, String phongHoc, int hocKy, int namHoc) {
        String sql = "SELECT * FROM LopHocPhan WHERE CaHoc = ? AND PhongHoc = ? AND HocKy = ? AND NamHoc = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, caHoc);
            pstmt.setString(2, phongHoc);
            pstmt.setInt(3, hocKy);
            pstmt.setInt(4, namHoc);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Trả về true nếu đã có lớp học
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra trùng phòng: " + e.getMessage());
        }
        return false;
    }

    // --- BỔ SUNG: KIỂM TRA GIẢNG VIÊN BẬN ---
    public boolean checkGiangVienBan(String maNV, String caHoc, int hocKy, int namHoc) {
        String sql = "SELECT * FROM LopHocPhan WHERE MaNV = ? AND CaHoc = ? AND HocKy = ? AND NamHoc = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maNV);
            pstmt.setString(2, caHoc);
            pstmt.setInt(3, hocKy);
            pstmt.setInt(4, namHoc);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Trả về true nếu giảng viên đã có lịch dạy ca này
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra lịch giảng viên: " + e.getMessage());
        }
        return false;
    }
}
