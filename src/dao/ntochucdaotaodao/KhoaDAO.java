package dao.ntochucdaotaodao;

import database.KNDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ntochucdaotao.Khoa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        // Đổi DELETE thành UPDATE isDeleted = 1
        String sql = "UPDATE Khoa SET isDeleted = 1 WHERE MaKhoa = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, maKhoa);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Da chuyen khoa " + maKhoa + " vao thung rac (Xoa mem)!");
            } else {
                System.out.println("Thong bao: Khong tim thay MaKhoa de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa mem khoa: " + e.getMessage());
        }
    }

    public ObservableList<Khoa> searchKhoa(String keyword) {
        ObservableList<Khoa> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Khoa WHERE MaKhoa LIKE ? OR TenKhoa LIKE ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Khoa(rs.getString("MaKhoa"), rs.getString("TenKhoa")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Lấy toàn bộ danh sách Khoa
    public ObservableList<Khoa> getAllKhoa() {
        ObservableList<Khoa> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Khoa";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Khoa(rs.getString("MaKhoa"), rs.getString("TenKhoa")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
