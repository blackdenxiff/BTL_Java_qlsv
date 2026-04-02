package dao.quanlydao;

import database.KNDatabase;
import model.quanly.DangKi;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DangKiDAO {

    // 1. Ham Them Dang Ky (Insert)
    public void insertDangKy(DangKi dk) {
        String sql = "INSERT INTO DangKy (maSV, maLHP, diemSo, trangThai) VALUES (?, ?, ?, ?)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, dk.getMaSV());
            pstmt.setString(2, dk.getMaLHP());
            pstmt.setDouble(3, dk.getDiemSo());
            pstmt.setString(4, dk.getTrangThai());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Them dang ky cho sinh vien " + dk.getMaSV() + " thanh cong!");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi them dang ky: " + e.getMessage());
        }
    }

    // Thuong dung de cap nhat diem so hoac trang thai hoc tap
    public void updateDangKy(DangKi dk) {
        String sql = "UPDATE DangKy SET diemSo = ?, trangThai = ? WHERE maSV = ? AND maLHP = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setDouble(1, dk.getDiemSo());
            pstmt.setString(2, dk.getTrangThai());

            // Dieu kien WHERE (Dung ca 2 khoa de xac dinh dung ban ghi)
            pstmt.setString(3, dk.getMaSV());
            pstmt.setString(4, dk.getMaLHP());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Cap nhat diem/trang thai cho sinh vien " + dk.getMaSV() + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay thong tin dang ky de cap nhat.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi cap nhat dang ky: " + e.getMessage());
        }
    }

    public void deleteDangKy(String maSV, String maLHP) {
        String sql = "DELETE FROM DangKy WHERE maSV = ? AND maLHP = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, maSV);
            pstmt.setString(2, maLHP);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Xoa dang ky mon hoc cua sinh vien " + maSV + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay thong tin dang ky de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa dang ky: " + e.getMessage());
        }
    }
    // --- BỔ SUNG: LẤY DANH SÁCH SINH VIÊN THEO LỚP HỌC PHẦN ---
    // Cần import model.nconnguoi.SinhVien và java.util.List, ArrayList
    public java.util.List<model.nconnguoi.SinhVien> getSinhVienByLHP(String maLHP) {
        java.util.List<model.nconnguoi.SinhVien> list = new java.util.ArrayList<>();
        // JOIN bảng SinhVien và DangKy
        String sql = "SELECT sv.* FROM SinhVien sv INNER JOIN DangKy dk ON sv.MaSV = dk.MaSV WHERE dk.MaLHP = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maLHP);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.nconnguoi.SinhVien sv = new model.nconnguoi.SinhVien();
                    sv.setMaSV(rs.getString("MaSV"));
                    sv.setHoDem(rs.getString("HoDem"));
                    sv.setTen(rs.getString("Ten"));
                    sv.setMaLop(rs.getString("MaLop"));
                    list.add(sv);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách sinh viên theo LHP: " + e.getMessage());
        }
        return list;
    }
    // --- BỔ SUNG: LẤY DANH SÁCH BẢNG ĐIỂM CỦA 1 LỚP HỌC PHẦN ---
    // Import java.util.Map và java.util.HashMap ở đầu file nhé!
    public java.util.List<java.util.Map<String, Object>> getBangDiemByLHP(String maLHP) {
        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();

        // Dùng INNER JOIN để ghép thông tin sinh viên và điểm số của họ
        String sql = "SELECT sv.MaSV, sv.HoDem, sv.Ten, dk.diemSo, dk.trangThai " +
                "FROM DangKy dk INNER JOIN SinhVien sv ON dk.MaSV = sv.MaSV " +
                "WHERE dk.MaLHP = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maLHP);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> row = new java.util.HashMap<>();
                    row.put("maSV", rs.getString("MaSV"));
                    row.put("hoTen", rs.getString("HoDem") + " " + rs.getString("Ten"));
                    row.put("diemSo", rs.getDouble("diemSo"));
                    row.put("trangThai", rs.getString("trangThai"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy bảng điểm: " + e.getMessage());
        }
        return list;
    }
    // --- BỔ SUNG: TÍNH TỔNG TÍN CHỈ CỦA 1 SINH VIÊN ---
    public int tongTinChiSinhVienDangKy(String maSV) {
        int tongTinChi = 0;

        // ĐÃ SỬA: SUM(mh.SoTinChi) thành SUM(mh.SoTC)
        String sql = "SELECT SUM(mh.SoTC) AS TongTinChi " +
                "FROM DangKy dk " +
                "INNER JOIN LopHocPhan lhp ON dk.MaLHP = lhp.MaLHP " +
                "INNER JOIN MonHoc mh ON lhp.MaMH = mh.MaMH " +
                "WHERE dk.MaSV = ?";

        try (java.sql.Connection conn = database.KNDatabase.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maSV);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Ở đây dùng "TongTinChi" là đúng vì nó là tên cột bí danh (AS TongTinChi) từ lệnh SUM
                    tongTinChi = rs.getInt("TongTinChi");
                }
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Lỗi tính tổng tín chỉ: " + e.getMessage());
        }
        return tongTinChi;
    }

    // Tìm đến file DangKiDAO.java và cập nhật lại hàm này:
    public List<Map<String, Object>> getChiTietBangDiem(String maSV) {
        List<Map<String, Object>> list = new ArrayList<>();

        // Sử dụng LEFT JOIN để không bỏ lỡ bất kỳ môn nào đã đăng ký
        String sql = "SELECT dk.MaLHP, mh.MaMH, mh.TenMH, mh.SoTC, qd.DonGiaTinChi " +
                "FROM DangKy dk " +
                "LEFT JOIN LopHocPhan lhp ON dk.MaLHP = lhp.MaLHP " +
                "LEFT JOIN MonHoc mh ON lhp.MaMH = mh.MaMH " +
                "LEFT JOIN QuyDinhHocPhi qd ON lhp.NamHoc = qd.NamHoc AND lhp.HocKy = qd.HocKy " +
                "WHERE dk.MaSV = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                // Nếu bảng MonHoc bị thiếu, lấy tạm MaLHP làm tên để Admin vẫn nhận diện được
                String maMH = rs.getString("MaMH");
                row.put("maMH", maMH != null ? maMH : "LHP: " + rs.getString("MaLHP"));

                String tenMH = rs.getString("TenMH");
                row.put("tenMH", tenMH != null ? tenMH : "(Chưa cập nhật tên môn)");

                row.put("soTinChi", rs.getInt("SoTC"));

                long donGia = 0;
                if (rs.getObject("DonGiaTinChi") != null) {
                    donGia = rs.getLong("DonGiaTinChi");
                }
                row.put("donGiaApDung", donGia);

                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}