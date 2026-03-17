package dao.ntochucdaotaodao;

import database.KNDatabase;
import model.ntochucdaotao.ChuonTrinhDaoTao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChuongTrinhDaoTaoDAO {

    public void insertCTDT(ChuonTrinhDaoTao ctdt) {
        String sql = "INSERT INTO ChuongTrinhDaoTao (MaCTDT, TenCTDT, TongTinChi, NamApDung, MaKhoa) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, ctdt.getMaCTDT());
            pstmt.setString(2, ctdt.getTenCTDT());
            pstmt.setInt(3, ctdt.getTongTinChi());
            pstmt.setInt(4, ctdt.getNamApDung());
            pstmt.setString(5, ctdt.getMaKhoa());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Them chuong trinh dao tao " + ctdt.getMaCTDT() + " thanh cong!");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi them chuong trinh dao tao: " + e.getMessage());
        }
    }

    public void updateCTDT(ChuonTrinhDaoTao ctdt) {
        String sql = "UPDATE ChuongTrinhDaoTao SET TenCTDT = ?, TongTinChi = ?, NamApDung = ?, MaKhoa = ? WHERE MaCTDT = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, ctdt.getTenCTDT());
            pstmt.setInt(2, ctdt.getTongTinChi());
            pstmt.setInt(3, ctdt.getNamApDung());
            pstmt.setString(4, ctdt.getMaKhoa());

            // Dieu kien WHERE
            pstmt.setString(5, ctdt.getMaCTDT());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Cap nhat chuong trinh dao tao " + ctdt.getMaCTDT() + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay MaCTDT de cap nhat.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi cap nhat chuong trinh dao tao: " + e.getMessage());
        }
    }

    // 3. Ham Xoa (Delete)
    public void deleteCTDT(String maCTDT) {
        String sql = "DELETE FROM ChuongTrinhDaoTao WHERE MaCTDT = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, maCTDT);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Xoa chuong trinh dao tao " + maCTDT + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay MaCTDT de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa chuong trinh dao tao: " + e.getMessage());
            System.out.println("Luu y: Kiem tra xem co Lop Hanh Chinh nao dang thuoc chuong trinh nay khong.");
        }
    }
}
