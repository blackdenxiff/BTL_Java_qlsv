package dao.quanlydao;

import database.KNDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.quanly.LopHanhChinh;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LopHanhChinhDAO {

    // 1. Ham Them (Insert)
    public void insertLopHanhChinh(LopHanhChinh lhc) {
        String sql = "INSERT INTO LopHanhChinh (maLop, nienKhoa, tenLop, maCTDT, maNV) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, lhc.getMaLop());
            pstmt.setString(2, lhc.getNienKhoa());
            pstmt.setString(3, lhc.getTenLop());
            pstmt.setString(4, lhc.getMaCTDT());
            pstmt.setString(5, lhc.getMaNV());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Them lop hanh chinh " + lhc.getTenLop() + " thanh cong!");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi them lop hanh chinh: " + e.getMessage());
        }
    }

    // 2. Ham Sua (Update)
    public void updateLopHanhChinh(LopHanhChinh lhc) {
        String sql = "UPDATE LopHanhChinh SET nienKhoa = ?, tenLop = ?, maCTDT = ?, maNV = ? WHERE maLop = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, lhc.getNienKhoa());
            pstmt.setString(2, lhc.getTenLop());
            pstmt.setString(3, lhc.getMaCTDT());
            pstmt.setString(4, lhc.getMaNV());

            // Dieu kien WHERE
            pstmt.setString(5, lhc.getMaLop());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cap nhat lop hanh chinh " + lhc.getMaLop() + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay ma lop de cap nhat.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi cap nhat lop hanh chinh: " + e.getMessage());
        }
    }

    // 3. Ham Xoa (Delete)
    public void deleteLopHanhChinh(String maLop) {
        String sql = "DELETE FROM LopHanhChinh WHERE maLop = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, maLop);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Xoa lop hanh chinh " + maLop + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay ma lop de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa lop hanh chinh: " + e.getMessage());
            System.out.println("Luu y: Hay xoa het sinh vien thuoc lop nay truoc khi xoa lop.");
        }
    }

    public ObservableList<LopHanhChinh> getByCTDT(String maCTDT) {
        ObservableList<LopHanhChinh> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM LopHanhChinh WHERE MaCTDT = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maCTDT);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Giả sử Constructor: (maLop, nienKhoa, tenLop, maCTDT, maNV)
                list.add(new LopHanhChinh(
                        rs.getString("MaLop"),
                        rs.getString("NienKhoa"),
                        rs.getString("TenLop"),
                        rs.getString("MaCTDT"),
                        rs.getString("MaNV")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}