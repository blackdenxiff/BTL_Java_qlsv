package dao.ntochucdaotaodao;

import database.KNDatabase;
import model.ntochucdaotao.MonHoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MonHocDAO {

    public void insertMonHoc(MonHoc mh) {
        String sql = "INSERT INTO MonHoc (MaMH, TenMH, SoTC, LoaiMon, MaCTDT) VALUES (?, ?, ?, ?, ?)";

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

            // Dieu kien WHERE dua tren MaMH
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
        String sql = "DELETE FROM MonHoc WHERE MaMH = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, maMH);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Xoa mon hoc " + maMH + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay MaMH de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa mon hoc: " + e.getMessage());
            System.out.println("Luu y: Kiem tra xem mon hoc nay co dang duoc mo lop hoac co diem sinh vien khong.");
        }
    }
}