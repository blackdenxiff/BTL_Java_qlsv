package dao.nhethongdao;

import database.KNDatabase;
import model.nhethong.NguoiDung;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NguoiDungDAO {
    public void insertNguoiDung(NguoiDung nd) {
        String sql = "INSERT INTO NguoiDung (username, matKhau, usertype, maSV, maNV) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("Loi ket noi DB!");
                return;
            }

            pstmt.setString(1, nd.getUsername());
            pstmt.setString(2, nd.getMatKhau());
            pstmt.setString(3, nd.getUsertype());

            // 🔥 XỬ LÝ LOGIC THEO USERTYPE
            if (nd.getUsertype().equals("SV")) {
                pstmt.setString(4, nd.getMaSV());
                pstmt.setNull(5, java.sql.Types.VARCHAR);
            }
            else if (nd.getUsertype().equals("NV")) {
                pstmt.setNull(4, java.sql.Types.VARCHAR);
                pstmt.setString(5, nd.getMaNV());
            }
            else if (nd.getUsertype().equals("AD")) {
                pstmt.setNull(4, java.sql.Types.VARCHAR);
                pstmt.setNull(5, java.sql.Types.VARCHAR);
            }
            else {
                System.out.println("Loai nguoi dung khong hop le!");
                return;
            }

            pstmt.executeUpdate();
            System.out.println("Them tai khoan thanh cong!");

        } catch (SQLException e) {
            System.out.println("Loi them nguoi dung: " + e.getMessage());
        }
    }

    public void updateNguoiDung(NguoiDung nd) {
        String sql = "UPDATE NguoiDung SET matKhau = ?, usertype = ?, maSV = ?, maNV = ? WHERE username = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nd.getMatKhau());
            pstmt.setString(2, nd.getUsertype());

            if (nd.getUsertype().equals("SV")) {
                pstmt.setString(3, nd.getMaSV());
                pstmt.setNull(4, java.sql.Types.VARCHAR);
            }
            else if (nd.getUsertype().equals("NV")) {
                pstmt.setNull(3, java.sql.Types.VARCHAR);
                pstmt.setString(4, nd.getMaNV());
            }
            else if (nd.getUsertype().equals("AD")) {
                pstmt.setNull(3, java.sql.Types.VARCHAR);
                pstmt.setNull(4, java.sql.Types.VARCHAR);
            }

            pstmt.setString(5, nd.getUsername());

            pstmt.executeUpdate();
            System.out.println("Cap nhat thanh cong!");

        } catch (SQLException e) {
            System.out.println("Loi update: " + e.getMessage());
        }
    }

    public boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM NguoiDung WHERE username = ? AND matKhau = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserType(String username) {
        String sql = "SELECT usertype FROM NguoiDung WHERE username = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("usertype");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 1. Lấy thông tin một người dùng theo username
    public NguoiDung getNguoiDungByUsername(String username) {
        String sql = "SELECT * FROM NguoiDung WHERE username = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                NguoiDung nd = new NguoiDung();
                nd.setUsername(rs.getString("username"));
                nd.setMatKhau(rs.getString("matKhau"));
                nd.setUsertype(rs.getString("usertype"));
                nd.setMaSV(rs.getString("maSV"));
                nd.setMaNV(rs.getString("maNV"));
                return nd;
            }
        } catch (SQLException e) {
            System.out.println("Loi lay nguoi dung: " + e.getMessage());
        }
        return null; // Trả về null nếu không tìm thấy
    }

    // 2. Lấy danh sách tất cả người dùng
    public List<NguoiDung> getAllNguoiDung() {
        List<NguoiDung> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM NguoiDung";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                NguoiDung nd = new NguoiDung();
                nd.setUsername(rs.getString("username"));
                nd.setMatKhau(rs.getString("matKhau"));
                nd.setUsertype(rs.getString("usertype"));
                nd.setMaSV(rs.getString("maSV"));
                nd.setMaNV(rs.getString("maNV"));

                danhSach.add(nd);
            }
        } catch (SQLException e) {
            System.out.println("Loi lay danh sach: " + e.getMessage());
        }
        return danhSach;
    }

}
