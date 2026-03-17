package dao.ntochucdaotaodao;

import database.KNDatabase;
import model.ntochucdaotao.Khoa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KhoaDAO {
    public void insertKhoa(Khoa k) {
        String sql = "INSERT INTO Khoa (MaKhoa, TenKhoa) VALUES (?, ?)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, k.getMaKhoa());
            pstmt.setString(2, k.getTenKhoa());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Them khoa " + k.getTenKhoa() + " thanh cong!");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi them khoa: " + e.getMessage());
        }
    }

    public void updateKhoa(Khoa k) {
        String sql = "UPDATE Khoa SET TenKhoa = ? WHERE MaKhoa = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, k.getTenKhoa());
            pstmt.setString(2, k.getMaKhoa());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Cap nhat khoa " + k.getMaKhoa() + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay MaKhoa de cap nhat.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi cap nhat khoa: " + e.getMessage());
        }
    }

    public void deleteKhoa(String maKhoa) {
        String sql = "DELETE FROM Khoa WHERE MaKhoa = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, maKhoa);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Xoa khoa " + maKhoa + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay MaKhoa de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa khoa: " + e.getMessage());
            System.out.println("Luu y: Khoa nay co the dang chua Nhan vien hoac Lop hoc. Ban phai xoa du lieu con truoc.");
        }
    }
}
