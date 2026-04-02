package dao.ntochucdaotaodao;

import database.KNDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ntochucdaotao.ChuonTrinhDaoTao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        // Đổi DELETE thành UPDATE isDeleted = 1
        String sql = "UPDATE ChuongTrinhDaoTao SET isDeleted = 1 WHERE MaCTDT = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, maCTDT);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Da chuyen CTDT " + maCTDT + " vao thung rac (Xoa mem)!");
            } else {
                System.out.println("Thong bao: Khong tim thay MaCTDT de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa mem CTDT: " + e.getMessage());
        }
    }

    public ObservableList<ChuonTrinhDaoTao> searchCTDT(String keyword) {
        ObservableList<ChuonTrinhDaoTao> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM ChuongTrinhDaoTao WHERE MaCTDT LIKE ? OR TenCTDT LIKE ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%" );
            pstmt.setString(2, "%" + keyword + "%" );
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ChuonTrinhDaoTao(
                        rs.getString("MaCTDT"), rs.getString("TenCTDT"),
                        rs.getInt("TongTinChi"), rs.getInt("NamApDung"), rs.getString("MaKhoa")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public ObservableList<ChuonTrinhDaoTao> getCTDTByKhoa(String maKhoa) {
        ObservableList<ChuonTrinhDaoTao> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM ChuongTrinhDaoTao WHERE MaKhoa = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maKhoa);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ChuonTrinhDaoTao(
                        rs.getString("MaCTDT"), rs.getString("TenCTDT"),
                        rs.getInt("TongTinChi"), rs.getInt("NamApDung"), rs.getString("MaKhoa")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public ObservableList<ChuonTrinhDaoTao> getAllCTDT() {
        ObservableList<ChuonTrinhDaoTao> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM ChuongTrinhDaoTao";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new ChuonTrinhDaoTao(
                        rs.getString("MaCTDT"),
                        rs.getString("TenCTDT"),
                        rs.getInt("TongTinChi"),
                        rs.getInt("NamApDung"),
                        rs.getString("MaKhoa")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
