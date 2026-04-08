package dao.quanlydao;

import database.KNDatabase;
import model.quanly.DangKi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DangKiDAO {

    public void insertDangKy(DangKi dk) {
        String sql = "INSERT INTO DangKy (maSV, maLHP, diemSo, trangThai) VALUES (?, ?, ?, ?)";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return;
            pstmt.setString(1, dk.getMaSV());
            pstmt.setString(2, dk.getMaLHP());
            pstmt.setDouble(3, dk.getDiemSo());
            pstmt.setString(4, dk.getTrangThai());
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }

    public void updateDangKy(DangKi dk) {
        String sql = "UPDATE DangKy SET diemSo = ?, trangThai = ? WHERE maSV = ? AND maLHP = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return;
            pstmt.setDouble(1, dk.getDiemSo());
            pstmt.setString(2, dk.getTrangThai());
            pstmt.setString(3, dk.getMaSV());
            pstmt.setString(4, dk.getMaLHP());
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }

    public void deleteDangKy(String maSV, String maLHP) {
        String sql = "DELETE FROM DangKy WHERE maSV = ? AND maLHP = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return;
            pstmt.setString(1, maSV);
            pstmt.setString(2, maLHP);
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }

    public List<model.nconnguoi.SinhVien> getSinhVienByLHP(String maLHP) {
        List<model.nconnguoi.SinhVien> list = new ArrayList<>();
        String sql = "SELECT sv.* FROM SinhVien sv INNER JOIN DangKy dk ON sv.MaSV = dk.MaSV WHERE dk.MaLHP = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maLHP);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.nconnguoi.SinhVien sv = new model.nconnguoi.SinhVien();
                    sv.setMaSV(rs.getString("MaSV"));
                    sv.setHoDem(rs.getString("HoDem"));
                    sv.setTen(rs.getString("Ten"));
                    sv.setMaLop(rs.getString("MaLop"));
                    list.add(sv);
                }
            }
        } catch (SQLException e) { }
        return list;
    }

    public List<Map<String, Object>> getBangDiemByLHP(String maLHP) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT sv.MaSV, sv.HoDem, sv.Ten, dk.diemSo, dk.trangThai " +
                "FROM DangKy dk INNER JOIN SinhVien sv ON dk.MaSV = sv.MaSV WHERE dk.MaLHP = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maLHP);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("maSV", rs.getString("MaSV"));
                    row.put("hoTen", rs.getString("HoDem") + " " + rs.getString("Ten"));
                    row.put("diemSo", rs.getDouble("diemSo"));
                    row.put("trangThai", rs.getString("trangThai"));
                    list.add(row);
                }
            }
        } catch (SQLException e) { }
        return list;
    }

    public int tongTinChiSinhVienDangKy(String maSV) {
        int tongTinChi = 0;
        String sql = "SELECT SUM(mh.SoTC) AS TongTinChi FROM DangKy dk " +
                "INNER JOIN LopHocPhan lhp ON dk.MaLHP = lhp.MaLHP " +
                "INNER JOIN MonHoc mh ON lhp.MaMH = mh.MaMH WHERE dk.MaSV = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maSV);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) tongTinChi = rs.getInt("TongTinChi");
            }
        } catch (SQLException e) { }
        return tongTinChi;
    }

    public List<Map<String, Object>> getChiTietBangDiem(String maSV) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT dk.MaLHP, mh.MaMH, mh.TenMH, mh.SoTC, qd.DonGiaTinChi " +
                "FROM DangKy dk " +
                "LEFT JOIN LopHocPhan lhp ON dk.MaLHP = lhp.MaLHP " +
                "LEFT JOIN MonHoc mh ON lhp.MaMH = mh.MaMH " +
                "LEFT JOIN QuyDinhHocPhi qd ON CAST(lhp.NamHoc AS NVARCHAR(20)) = qd.NamHoc AND lhp.HocKy = qd.HocKy " +
                "WHERE dk.MaSV = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                String maMH = rs.getString("MaMH");
                row.put("maMH", maMH != null ? maMH : "LHP: " + rs.getString("MaLHP"));
                String tenMH = rs.getString("TenMH");
                row.put("tenMH", tenMH != null ? tenMH : "(Chưa cập nhật tên môn)");
                row.put("soTinChi", rs.getInt("SoTC"));
                long donGia = 0;
                if (rs.getObject("DonGiaTinChi") != null) donGia = rs.getLong("DonGiaTinChi");
                row.put("donGiaApDung", donGia);
                list.add(row);
            }
        } catch (SQLException e) { }
        return list;
    }

    public boolean checkDaDangKy(String maSV, String maLHP) {
        String sql = "SELECT * FROM DangKy WHERE maSV = ? AND maLHP = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maSV);
            pstmt.setString(2, maLHP);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { }
        return false;
    }
    // Kiểm tra xem môn học này đã nộp tiền chưa
    public boolean checkDaNopTien(String maSV, String maLHP) {
        // Logic: Nếu sinh viên có phiếu thu cho môn này (hoặc tổng tiền thu >= tiền môn này)
        // Ở đây mình dùng logic đơn giản: Nếu TrangThai trong bảng DangKy là 'Đã đóng tiền'
        // Hoặc bạn có thể check trong bảng PhieuThu tùy theo cấu trúc DB của bạn.

        String sql = "SELECT COUNT(*) FROM DangKy WHERE MaSV = ? AND MaLHP = ? AND TrangThai = N'Đã đóng tiền'";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ps.setString(2, maLHP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    // =========================================================================
    // =============== THUẬT TOÁN ĐỔI LỊCH TỰ ĐỘNG CỦA SANG ====================
    // =========================================================================

    // 1. Hàm bóc tách Thứ từ chuỗi (Chia đôi chuỗi để không đọc nhầm Ca)
    private int extractDay(String caHoc) {
        if (caHoc == null || !caHoc.contains("-")) return 2; // Mặc định thứ 2
        String dayPart = caHoc.split("-")[0]; // Chỉ lấy khúc đầu "Thu 2 "

        if (dayPart.contains("CN") || dayPart.contains("Chủ")) return 8;
        if (dayPart.contains("3")) return 3;
        if (dayPart.contains("4")) return 4;
        if (dayPart.contains("5")) return 5;
        if (dayPart.contains("6")) return 6;
        if (dayPart.contains("7")) return 7;
        return 2;
    }

    // 2. Hàm bóc tách Ca học từ chuỗi
    private int extractShift(String caHoc) {
        if (caHoc == null || !caHoc.contains("-")) return 1;
        String shiftPart = caHoc.split("-")[1]; // Chỉ lấy khúc sau " Ca 1"

        if (shiftPart.contains("2")) return 2;
        if (shiftPart.contains("3")) return 3;
        if (shiftPart.contains("4")) return 4;
        return 1;
    }

    // 3. Ghép lại chuỗi chuẩn để lưu vào DB
    private String formatCaHoc(int day, int shift) {
        String dayStr = (day == 8) ? "CN" : "Thu " + day;
        return dayStr + " - Ca " + shift;
    }

    // 4. Quét DB xem sinh viên đã có môn nào trong khung giờ này chưa
    public boolean isConflict(String maSV, int day, int shift) {
        String caHocCheck = formatCaHoc(day, shift);
        String sql = "SELECT COUNT(*) FROM DangKy dk " +
                "INNER JOIN LopHocPhan lhp_cu ON dk.MaLHP = lhp_cu.MaLHP " +
                "WHERE dk.MaSV = ? AND lhp_cu.CaHoc = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ps.setString(2, caHocCheck);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { }
        return false;
    }

    // 5. THUẬT TOÁN TÌM LỊCH TRỐNG
    public String findAndAutoFixSchedule(String maSV, String caHocGoc) {
        if (caHocGoc == null || caHocGoc.isEmpty()) return "FULL";

        int startDay = extractDay(caHocGoc);
        int startShift = extractShift(caHocGoc);

        // BƯỚC 1: Kiểm tra ca gốc, nếu không trùng thì lấy luôn
        if (!isConflict(maSV, startDay, startShift)) {
            return caHocGoc;
        }

        // BƯỚC 2: Trừ lùi ca học xuống 1 (vd ca 3 -> ca 2 -> ca 1)
        for (int s = startShift - 1; s >= 1; s--) {
            if (!isConflict(maSV, startDay, s)) return formatCaHoc(startDay, s);
        }

        // BƯỚC 3: Nếu vẫn trùng, quay lại điểm gốc cộng tiến lên 4
        for (int s = startShift + 1; s <= 4; s++) {
            if (!isConflict(maSV, startDay, s)) return formatCaHoc(startDay, s);
        }

        // BƯỚC 4: Nếu từ ca 1 -> ca 4 của ngày đó đều kín, nhảy sang các ngày tiếp theo
        for (int d = 1; d <= 6; d++) {
            int nextDay = startDay + d;
            if (nextDay > 8) nextDay -= 7; // Quay vòng từ Chủ Nhật (8) về Thứ 2

            for (int s = 1; s <= 4; s++) { // Bắt đầu quét từ ca 1 ngày mới
                if (!isConflict(maSV, nextDay, s)) return formatCaHoc(nextDay, s);
            }
        }

        // BƯỚC 5: Quét sạch 7 ngày đều kín
        return "FULL";
    }

    // 6. Hàm đổi lịch học thực sự dưới Database
    public void updateCaHocLopHocPhan(String maLHP, String newCaHoc) {
        String sql = "UPDATE LopHocPhan SET CaHoc = ? WHERE MaLHP = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newCaHoc);
            ps.setString(2, maLHP);
            ps.executeUpdate();
        } catch (SQLException e) { }
    }
}