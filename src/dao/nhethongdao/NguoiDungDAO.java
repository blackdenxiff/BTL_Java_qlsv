package dao.nhethongdao;

import database.KNDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.nhethong.NguoiDung;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NguoiDungDAO {

    // 1. Lấy danh sách tài khoản đang hoạt động (isDeleted = 0)
    public ObservableList<NguoiDung> getAllNguoiDung() {
        ObservableList<NguoiDung> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM NguoiDung WHERE (isDeleted = 0 OR isDeleted IS NULL)";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToNguoiDung(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 2. [HÀM MỚI] Lấy danh sách tài khoản trong thùng rác (isDeleted = 1)
    public ObservableList<NguoiDung> getAllDeletedNguoiDung() {
        ObservableList<NguoiDung> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM NguoiDung WHERE isDeleted = 1";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToNguoiDung(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 3. Xóa mềm tài khoản + Cập nhật trạng thái Sinh viên thành "Thôi học"
    public void deleteNguoiDung(String username) {
        String sqlUser = "UPDATE NguoiDung SET isDeleted = 1 WHERE username = ?";
        String sqlSV = "UPDATE SinhVien SET TrangThaiSV = N'Thôi học' WHERE maSV = (SELECT maSV FROM NguoiDung WHERE username = ?)";

        try (Connection conn = KNDatabase.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(sqlUser);
                 PreparedStatement p2 = conn.prepareStatement(sqlSV)) {
                p1.setString(1, username);
                p2.setString(1, username);
                p1.executeUpdate();
                p2.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // 4. Khôi phục tài khoản + Cập nhật trạng thái Sinh viên thành "Đang học"
    public boolean restoreNguoiDung(String username) {
        String sqlUser = "UPDATE NguoiDung SET isDeleted = 0 WHERE username = ?";
        String sqlSV = "UPDATE SinhVien SET TrangThaiSV = N'Đang học' WHERE maSV = (SELECT maSV FROM NguoiDung WHERE username = ?)";

        try (Connection conn = KNDatabase.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(sqlUser);
                 PreparedStatement p2 = conn.prepareStatement(sqlSV)) {
                p1.setString(1, username);
                p2.setString(1, username);
                int res = p1.executeUpdate();
                p2.executeUpdate();
                conn.commit();
                return res > 0;
            } catch (SQLException e) {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) { return false; }
    }

    // 5. [HÀM MỚI] Reset toàn bộ mật khẩu về ngày sinh
    public int resetAllPasswordsToDefault() {
        int affectedRows = 0;
        String sqlSV = "UPDATE NguoiDung SET matKhau = SV.ngaySinh FROM NguoiDung INNER JOIN SinhVien SV ON NguoiDung.maSV = SV.maSV";
        String sqlNV = "UPDATE NguoiDung SET matKhau = NV.ngaySinh FROM NguoiDung INNER JOIN NhanVien NV ON NguoiDung.maNV = NV.maNV";

        try (Connection conn = KNDatabase.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtSV = conn.prepareStatement(sqlSV);
                 PreparedStatement pstmtNV = conn.prepareStatement(sqlNV)) {
                affectedRows += pstmtSV.executeUpdate();
                affectedRows += pstmtNV.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return affectedRows;
    }

    // 6. Cập nhật tài khoản (Giữ nguyên đầu vào/đầu ra)
    public void updateNguoiDung(NguoiDung nd) {
        String sql = "UPDATE NguoiDung SET matKhau = ?, usertype = ?, maSV = ?, maNV = ? WHERE username = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nd.getMatKhau());
            pstmt.setString(2, nd.getUsertype());
            pstmt.setString(3, nd.getMaSV());
            pstmt.setString(4, nd.getMaNV());
            pstmt.setString(5, nd.getUsername());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // 7. Các hàm hỗ trợ khác (Check Login, Tìm kiếm, Tạo tài khoản)
    public boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM NguoiDung WHERE username = ? AND matKhau = ? AND (isDeleted = 0 OR isDeleted IS NULL)";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            return pstmt.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    public NguoiDung findByUsername(String username) {
        String sql = "SELECT * FROM NguoiDung WHERE username = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapResultSetToNguoiDung(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public int createAccountsForAll() {
        int count = 0;
        String sqlSV = "SELECT maSV, ngaySinh FROM SinhVien WHERE maSV NOT IN (SELECT username FROM NguoiDung)";
        String sqlNV = "SELECT maNV, ngaySinh FROM NhanVien WHERE maNV NOT IN (SELECT username FROM NguoiDung)";
        try (Connection conn = KNDatabase.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlSV); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    insertNguoiDung(new NguoiDung(rs.getString("maSV"), rs.getString("ngaySinh"), "SV", rs.getString("maSV"), null));
                    count++;
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlNV); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    insertNguoiDung(new NguoiDung(rs.getString("maNV"), rs.getString("ngaySinh"), "NV", null, rs.getString("maNV")));
                    count++;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return count;
    }

    public boolean createSingleAccount(String id) {
        NguoiDung existing = findByUsername(id);
        if (existing != null) return false;

        String sql = "SELECT ngaySinh, 'SV' as type FROM SinhVien WHERE maSV = ? UNION SELECT ngaySinh, 'NV' FROM NhanVien WHERE maNV = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String type = rs.getString("type");
                insertNguoiDung(new NguoiDung(id, rs.getString("ngaySinh"), type, type.equals("SV")?id:null, type.equals("NV")?id:null));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public void insertNguoiDung(NguoiDung nd) {
        String sql = "INSERT INTO NguoiDung (username, matKhau, usertype, maSV, maNV, isDeleted) VALUES (?, ?, ?, ?, ?, 0)";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nd.getUsername());
            pstmt.setString(2, nd.getMatKhau());
            pstmt.setString(3, nd.getUsertype());
            pstmt.setString(4, nd.getMaSV());
            pstmt.setString(5, nd.getMaNV());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean resetPasswordToBirthdate(String username, String type) {
        String sqlFind = type.equals("SV") ? "SELECT ngaySinh FROM SinhVien WHERE maSV = ?" : "SELECT ngaySinh FROM NhanVien WHERE maNV = ?";
        String sqlUpdate = "UPDATE NguoiDung SET matKhau = ? WHERE username = ?";
        try (Connection conn = KNDatabase.getConnection()) {
            String dob = "";
            try (PreparedStatement p1 = conn.prepareStatement(sqlFind)) {
                p1.setString(1, username);
                ResultSet rs = p1.executeQuery();
                if (rs.next()) dob = rs.getString("ngaySinh");
            }
            if (!dob.isEmpty()) {
                try (PreparedStatement p2 = conn.prepareStatement(sqlUpdate)) {
                    p2.setString(1, dob);
                    p2.setString(2, username);
                    return p2.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // Hàm phụ trợ để tránh lặp code mapping
    private NguoiDung mapResultSetToNguoiDung(ResultSet rs) throws SQLException {
        NguoiDung nd = new NguoiDung();
        nd.setUsername(rs.getString("username"));
        nd.setMatKhau(rs.getString("matKhau"));
        nd.setUsertype(rs.getString("usertype"));
        nd.setMaSV(rs.getString("maSV"));
        nd.setMaNV(rs.getString("maNV"));
        return nd;
    }

    public String getUserType(String username) {
        // Truy vấn lấy cột usertype và lọc tài khoản chưa bị xóa (isDeleted = 0)
        String sql = "SELECT usertype FROM NguoiDung WHERE username = ? AND (isDeleted = 0 OR isDeleted IS NULL)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Trả về giá trị như "AD", "SV", "NV" hoặc "GV"
                return rs.getString("usertype");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu không tìm thấy hoặc tài khoản đã bị xóa mềm
    }

    // Hàm kiểm tra xem tài khoản có đang bị khóa (isDeleted = 1) hay không
    public boolean isAccountLocked(String username) {
        String sql = "SELECT isDeleted FROM NguoiDung WHERE username = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Nếu isDeleted = 1 tức là tài khoản đã bị đưa vào thùng rác / khóa
                return rs.getInt("isDeleted") == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}