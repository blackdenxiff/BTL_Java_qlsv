package dao.ndialydao;

import database.KNDatabase;
import model.ndialy.QuanHuyen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QuanHuyenDAO {

    public void insertQuanHuyen(QuanHuyen qh) {
        String sql = "INSERT INTO QuanHuyen (MaHuyen, TenHuyen, MaTinh) VALUES (?, ?, ?)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("Loi: Khong the ket noi co so du lieu!");
                return;
            }

            pstmt.setString(1, qh.getMaHuyen());
            pstmt.setString(2, qh.getTenHuyen());
            pstmt.setString(3, qh.getMaTinh());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Them quan huyen " + qh.getTenHuyen() + " thanh cong!");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi them quan huyen: " + e.getMessage());
        }
    }

    public void updateQuanHuyen(QuanHuyen qh) {
        // Chỉ cập nhật TenHuyen và MaTinh dựa trên khóa chính là MaHuyen
        String sql = "UPDATE QuanHuyen SET TenHuyen = ?, MaTinh = ? WHERE MaHuyen = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, qh.getTenHuyen());
            pstmt.setString(2, qh.getMaTinh());

            // Điều kiện WHERE
            pstmt.setString(3, qh.getMaHuyen());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cap nhat quan huyen " + qh.getMaHuyen() + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay quan huyen ma " + qh.getMaHuyen());
            }

        } catch (SQLException e) {
            System.out.println("Loi khi cap nhat quan huyen: " + e.getMessage());
        }
    }

    // 3. Hàm Xóa (Delete)
    public void deleteQuanHuyen(String maHuyen) {
        String sql = "DELETE FROM QuanHuyen WHERE MaHuyen = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, maHuyen);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Xoa quan huyen " + maHuyen + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay quan huyen ma " + maHuyen + " de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa quan huyen: " + e.getMessage());
            System.out.println("Luu y: Kiem tra xem co sinh vien nao dang o huyen nay khong truoc khi xoa.");
        }
    }
}
