package dao.nconnguoidao;

import database.KNDatabase;
import model.nconnguoi.SinhVien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SinhVienDAO {

    public void insertSinhVien(SinhVien sv) {
        String sql = "INSERT INTO SinhVien (MaSV, HoDem, Ten, NgaySinh, GioiTinh, NamNhapHoc, MaLop, SDT, Email, TrangThaiSV, MaHuyen) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("Không thể thiết lập kết nối tới cơ sở dữ liệu!");
                return;
            }

            // Truyền tham số vào câu lệnh SQL
            pstmt.setString(1, sv.getMaSV());
            pstmt.setString(2, sv.getHoDem());
            pstmt.setString(3, sv.getTen());

            // Chuyển từ java.time.LocalDate sang java.sql.Date
            pstmt.setDate(4, Date.valueOf(sv.getNgaySinh()));

            pstmt.setString(5, sv.getGioiTinh());
            pstmt.setInt(6, sv.getNamNhapHoc());
            pstmt.setString(7, sv.getMaLop());
            pstmt.setString(8, sv.getSdt());
            pstmt.setString(9, sv.getEmail());
            pstmt.setString(10, sv.getTrangThaiSV());
            pstmt.setString(11, sv.getMaHuyen());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Thêm sinh viên thành công!");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi chèn dữ liệu: " + e.getMessage());
        }
    }

    public void deleteSinhVien(String maSV) {
        String sql = "DELETE FROM SinhVien WHERE MaSV = ?";

        // Tự động mở và đóng kết nối nhờ try-with-resources
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            // Truyền MaSV vào dấu ?
            pstmt.setString(1, maSV);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Xóa sinh viên " + maSV + " thành công!");
            } else {
                System.out.println("Không tìm thấy sinh viên có mã: " + maSV);
            }

        } catch (SQLException e) {
            // Lỗi này thường xảy ra nếu sinh viên đã có dữ liệu ở bảng DangKy (Ràng buộc khóa ngoại)
            System.err.println("Lỗi khi xóa dữ liệu: " + e.getMessage());
        }
    }

    public void updateSinhVien(SinhVien sv) {
        // Câu lệnh SQL cập nhật tất cả thông tin ngoại trừ MaSV
        String sql = "UPDATE SinhVien SET HoDem = ?, Ten = ?, NgaySinh = ?, GioiTinh = ?, "
                + "NamNhapHoc = ?, MaLop = ?, SDT = ?, Email = ?, TrangThaiSV = ?, MaHuyen = ? "
                + "WHERE MaSV = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            // Set các giá trị mới từ đối tượng sv truyền vào
            pstmt.setString(1, sv.getHoDem());
            pstmt.setString(2, sv.getTen());

            if (sv.getNgaySinh() != null) {
                pstmt.setDate(3, java.sql.Date.valueOf(sv.getNgaySinh()));
            } else {
                pstmt.setNull(3, java.sql.Types.DATE);
            }

            pstmt.setString(4, sv.getGioiTinh());
            pstmt.setInt(5, sv.getNamNhapHoc());
            pstmt.setString(6, sv.getMaLop());
            pstmt.setString(7, sv.getSdt());
            pstmt.setString(8, sv.getEmail());
            pstmt.setString(9, sv.getTrangThaiSV());
            pstmt.setString(10, sv.getMaHuyen());

            // Điều kiện WHERE
            pstmt.setString(11, sv.getMaSV());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cập nhật thông tin sinh viên " + sv.getMaSV() + " thành công!");
            } else {
                System.out.println("Không tìm thấy sinh viên mã " + sv.getMaSV() + " để sửa.");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật dữ liệu: " + e.getMessage());
        }
    }

    // Lay toan bo danh sach sinh vien
    public List<SinhVien> getAllSinhVien() {
        List<SinhVien> list = new ArrayList<>();
        String sql = "SELECT * FROM SinhVien";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                SinhVien sv = new SinhVien();
                sv.setMaSV(rs.getString("MaSV"));
                sv.setHoDem(rs.getString("HoDem"));
                sv.setTen(rs.getString("Ten"));

                // Chuyen doi tu sql.Date sang LocalDate
                if (rs.getDate("NgaySinh") != null) {
                    sv.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                }

                sv.setGioiTinh(rs.getString("GioiTinh"));
                sv.setMaLop(rs.getString("MaLop"));
                sv.setMaHuyen(rs.getString("MaHuyen"));
                sv.setSdt(rs.getString("SDT"));
                sv.setEmail(rs.getString("Email"));

                list.add(sv);
            }
        } catch (SQLException e) {
            System.out.println("Loi khi lay danh sach sinh vien: " + e.getMessage());
        }
        return list;
    }
    //tim 1 sinh vien
    public SinhVien getSinhVienByMa(String maSV) {
        String sql = "SELECT * FROM SinhVien WHERE MaSV = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maSV);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    SinhVien sv = new SinhVien();
                    sv.setMaSV(rs.getString("MaSV"));
                    sv.setHoDem(rs.getString("HoDem"));
                    sv.setTen(rs.getString("Ten"));
                    if (rs.getDate("NgaySinh") != null) {
                        sv.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                    }
                    sv.setGioiTinh(rs.getString("GioiTinh"));
                    sv.setMaLop(rs.getString("MaLop"));
                    sv.setMaHuyen(rs.getString("MaHuyen"));
                    sv.setSdt(rs.getString("SDT"));
                    sv.setEmail(rs.getString("Email"));
                    return sv;
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi khi tim sinh vien theo ma: " + e.getMessage());
        }
        return null;
    }
}
