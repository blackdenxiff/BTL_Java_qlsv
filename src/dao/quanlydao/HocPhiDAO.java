package dao.quanlydao;

import database.KNDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HocPhiDAO {

    // 1. Lấy đơn giá mới nhất (Dựa theo Năm học lớn nhất và Học kỳ lớn nhất)
    public long getDonGiaHienTai() {
        long donGia = 0;
        String sql = "SELECT TOP 1 DonGiaTinChi FROM QuyDinhHocPhi ORDER BY NamHoc DESC, HocKy DESC";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                donGia = rs.getLong("DonGiaTinChi");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donGia;
    }

    // 2. Lưu hoặc Cập nhật đơn giá cho 1 Học kỳ cụ thể (Cơ chế UPSERT)
    public boolean saveQuyDinhHocPhi(String namHoc, int hocKy, long donGia) {
        // Kiểm tra xem kỳ này đã có giá chưa
        String checkSql = "SELECT COUNT(*) FROM QuyDinhHocPhi WHERE NamHoc = ? AND HocKy = ?";
        String insertSql = "INSERT INTO QuyDinhHocPhi (NamHoc, HocKy, DonGiaTinChi) VALUES (?, ?, ?)";
        String updateSql = "UPDATE QuyDinhHocPhi SET DonGiaTinChi = ? WHERE NamHoc = ? AND HocKy = ?";

        try (Connection conn = KNDatabase.getConnection()) {
            boolean exists = false;
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, namHoc);
                psCheck.setInt(2, hocKy);
                ResultSet rs = psCheck.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) exists = true;
            }

            if (exists) {
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setLong(1, donGia);
                    psUpdate.setString(2, namHoc);
                    psUpdate.setInt(3, hocKy);
                    return psUpdate.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    psInsert.setString(1, namHoc);
                    psInsert.setInt(2, hocKy);
                    psInsert.setLong(3, donGia);
                    return psInsert.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}