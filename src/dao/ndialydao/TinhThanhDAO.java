package dao.ndialydao;

import database.KNDatabase;
import model.ndialy.QuanHuyen;
import model.ndialy.TinhThanh;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TinhThanhDAO {

    public void insertTinhThanh(TinhThanh tt) {
        String sql = "INSERT INTO TinhThanh (MaTinh, TenTinh) VALUES (?, ?)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("Loi: Khong the ket noi co so du lieu!");
                return;
            }

            pstmt.setString(1, tt.getMaTinh());
            pstmt.setString(2, tt.getTenTinh());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Them tinh thanh " + tt.getTenTinh() + " thanh cong!");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi them tinh thanh: " + e.getMessage());
        }
    }

    // 2. Ham Sua (Update)
    public void updateTinhThanh(TinhThanh tt) {
        String sql = "UPDATE TinhThanh SET TenTinh = ? WHERE MaTinh = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, tt.getTenTinh());
            pstmt.setString(2, tt.getMaTinh());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cap nhat tinh thanh " + tt.getMaTinh() + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay tinh thanh ma " + tt.getMaTinh());
            }

        } catch (SQLException e) {
            System.out.println("Loi khi cap nhat tinh thanh: " + e.getMessage());
        }
    }

    // 3. Ham Xoa (Delete)
    public void deleteTinhThanh(String maTinh) {
        String sql = "DELETE FROM TinhThanh WHERE MaTinh = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return;

            pstmt.setString(1, maTinh);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Xoa tinh thanh " + maTinh + " thanh cong!");
            } else {
                System.out.println("Thong bao: Khong tim thay tinh thanh ma " + maTinh + " de xoa.");
            }

        } catch (SQLException e) {
            System.out.println("Loi khi xoa tinh thanh: " + e.getMessage());
            System.out.println("Luu y: Tinh nay co the dang chua cac Quan Huyen. Ban phai xoa Quan Huyen truoc.");
        }
    }
}
