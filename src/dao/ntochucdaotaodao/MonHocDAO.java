package dao.ntochucdaotaodao;

import database.KNDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ntochucdaotao.MonHoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MonHocDAO {

    public void insertMonHoc(MonHoc mh) {
        // Bổ sung isDeleted = 0 khi thêm mới
        String sql = "INSERT INTO MonHoc (MaMH, TenMH, SoTC, LoaiMon, MaCTDT, isDeleted) VALUES (?, ?, ?, ?, ?, 0)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, mh.getMaMH());
            pstmt.setString(2, mh.getTenMH());
            pstmt.setInt(3, mh.getSoTC());
            pstmt.setString(4, mh.getLoaiMon());
            pstmt.setString(5, mh.getMaCTDT());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Them mon hoc " + mh.getTenMH() + " thanh cong!");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi them mon hoc: " + e.getMessage());
        }
    }

    public void updateMonHoc(MonHoc mh) {
        String sql = "UPDATE MonHoc SET TenMH = ?, SoTC = ?, LoaiMon = ?, MaCTDT = ? WHERE MaMH = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, mh.getTenMH());
            pstmt.setInt(2, mh.getSoTC());
            pstmt.setString(3, mh.getLoaiMon());
            pstmt.setString(4, mh.getMaCTDT());
            pstmt.setString(5, mh.getMaMH());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cap nhat mon hoc " + mh.getMaMH() + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay MaMH de cap nhat.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi cap nhat mon hoc: " + e.getMessage());
        }
    }

    public void deleteMonHoc(String maMH) {
        // Đã đổi DELETE thành UPDATE isDeleted = 1
        String sql = "UPDATE MonHoc SET isDeleted = 1 WHERE MaMH = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, maMH);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Da chuyen mon hoc " + maMH + " vao thung rac (Xoa mem)!");
            } else {
                System.out.println("Thong bao: Khong tim thay MaMH de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa mem mon hoc: " + e.getMessage());
        }
    }

    public ObservableList<MonHoc> searchMonHoc(String keyword) {
        ObservableList<MonHoc> list = FXCollections.observableArrayList();
        // Bổ sung điều kiện lọc isDeleted
        String sql = "SELECT * FROM MonHoc WHERE (MaMH LIKE ? OR TenMH LIKE ?) AND (isDeleted = 0 OR isDeleted IS NULL)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new MonHoc(
                        rs.getString("MaMH"), rs.getString("TenMH"),
                        rs.getInt("SoTC"), rs.getString("LoaiMon"), rs.getString("MaCTDT")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public ObservableList<MonHoc> getAllMonHoc() {
        ObservableList<MonHoc> list = FXCollections.observableArrayList();
        // Bổ sung điều kiện lọc isDeleted
        String sql = "SELECT * FROM MonHoc WHERE isDeleted = 0 OR isDeleted IS NULL";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new MonHoc(
                        rs.getString("MaMH"), rs.getString("TenMH"),
                        rs.getInt("SoTC"), rs.getString("LoaiMon"), rs.getString("MaCTDT")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Lấy Môn học theo CTDT (Dùng cho TreeView khi nhấn vào CTDT)
    public ObservableList<MonHoc> getMonHocByCTDT(String maCTDT) {
        ObservableList<MonHoc> list = FXCollections.observableArrayList();
        // Bổ sung điều kiện lọc isDeleted
        String sql = "SELECT * FROM MonHoc WHERE MaCTDT = ? AND (isDeleted = 0 OR isDeleted IS NULL)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maCTDT);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new MonHoc(
                        rs.getString("MaMH"), rs.getString("TenMH"),
                        rs.getInt("SoTC"), rs.getString("LoaiMon"), rs.getString("MaCTDT")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}