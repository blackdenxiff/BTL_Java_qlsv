package dao.nconnguoidao;

import database.KNDatabase;
import model.nconnguoi.NhanVien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    public void insertNhanVien(NhanVien nv){
            String sql = "INSERT INTO NhanVien (MaNV, HoDem, Ten, NgaySinh, GioiTinh, LoaiNV, MaKhoa, HocVan, SDT, Email)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = KNDatabase.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)){

                if (conn == null) {
                    System.err.println("Không thể thiết lập kết nối tới cơ sở dữ liệu!");
                    return;
                }

                // 1. MaNV
                pstmt.setString(1, nv.getMaNV());
                // 2. HoDem
                pstmt.setString(2, nv.getHoDem());
                // 3. Ten
                pstmt.setString(3, nv.getTen());

                // 4. NgaySinh (Chuyển đổi từ LocalDate sang sql.Date)
                if (nv.getNgaySinh() != null) {
                    pstmt.setDate(4, Date.valueOf(nv.getNgaySinh()));
                } else {
                    pstmt.setNull(4, java.sql.Types.DATE);
                }

                // 5. GioiTinh
                pstmt.setString(5, nv.getGioiTinh());
                // 6. LoaiNV (Ví dụ: Giảng viên, Hành chính...)
                pstmt.setString(6, nv.getLoaiNV());
                // 7. MaKhoa (Khóa ngoại - phải tồn tại trong bảng Khoa)
                pstmt.setString(7, nv.getMaKhoa());
                // 8. HocVan
                pstmt.setString(8, nv.getHocVan());
                // 9. SDT
                pstmt.setString(9, nv.getSdt());
                // 10. Email
                pstmt.setString(10, nv.getEmail());

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Thêm nhân viên " + nv.getTen() + " thành công!");
                }
            } catch (SQLException e) {
                System.err.println("Lỗi khi chèn dữ liệu nhân viên: " + e.getMessage());
            }
        }

        public void updateNhanVien(NhanVien nv) {
        String sql = "UPDATE NhanVien SET HoDem = ?, Ten = ?, NgaySinh = ?, GioiTinh = ?, "
                + "LoaiNV = ?, MaKhoa = ?, HocVan = ?, SDT = ?, Email = ? "
                + "WHERE MaNV = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, nv.getHoDem());
            pstmt.setString(2, nv.getTen());

            if (nv.getNgaySinh() != null) {
                pstmt.setDate(3, java.sql.Date.valueOf(nv.getNgaySinh()));
            } else {
                pstmt.setNull(3, java.sql.Types.DATE);
            }

            pstmt.setString(4, nv.getGioiTinh());
            pstmt.setString(5, nv.getLoaiNV());
            pstmt.setString(6, nv.getMaKhoa());
            pstmt.setString(7, nv.getHocVan());
            pstmt.setString(8, nv.getSdt());
            pstmt.setString(9, nv.getEmail());

            // Điều kiện WHERE
            pstmt.setString(10, nv.getMaNV());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cập nhật nhân viên " + nv.getMaNV() + " thành công!");
            } else {
                System.out.println("Không tìm thấy nhân viên mã " + nv.getMaNV());
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật nhân viên: " + e.getMessage());
        }
    }

    public void deleteNhanVien(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE MaNV = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, maNV);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Xóa nhân viên " + maNV + " thành công!");
            } else {
                System.out.println("Không tìm thấy nhân viên mã " + maNV + " để xóa.");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa nhân viên: " + e.getMessage());
            System.err.println("Mẹo: Kiểm tra xem nhân viên này có đang quản lý lớp nào không trước khi xóa.");
        }
    }

    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("MaNV"));
                nv.setHoDem(rs.getString("HoDem"));
                nv.setTen(rs.getString("Ten"));

                if (rs.getDate("NgaySinh") != null) {
                    nv.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                }

                nv.setGioiTinh(rs.getString("GioiTinh"));
                nv.setLoaiNV(rs.getString("LoaiNV"));
                nv.setMaKhoa(rs.getString("MaKhoa"));
                nv.setHocVan(rs.getString("HocVan"));
                nv.setSdt(rs.getString("SDT"));
                nv.setEmail(rs.getString("Email"));

                list.add(nv);
            }
        } catch (SQLException e) {
            System.out.println("Loi khi lay danh sach nhan vien: " + e.getMessage());
        }
        return list;
    }

    public NhanVien getNhanVienByMa(String maNV) {
        String sql = "SELECT * FROM NhanVien WHERE MaNV = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNV);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("MaNV"));
                    nv.setHoDem(rs.getString("HoDem"));
                    nv.setTen(rs.getString("Ten"));
                    if (rs.getDate("NgaySinh") != null) {
                        nv.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                    }
                    nv.setGioiTinh(rs.getString("GioiTinh"));
                    nv.setLoaiNV(rs.getString("LoaiNV"));
                    nv.setMaKhoa(rs.getString("MaKhoa"));
                    nv.setHocVan(rs.getString("HocVan"));
                    nv.setSdt(rs.getString("SDT"));
                    nv.setEmail(rs.getString("Email"));
                    return nv;
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi khi tim nhan vien theo ma: " + e.getMessage());
        }
        return null;
    }


}
