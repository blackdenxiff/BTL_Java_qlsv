package database;

// Đảm bảo import đúng gói chứa lớp DAO của bạn
// ví dụ: import database.SinhVienDAO; 
import com.microsoft.sqlserver.jdbc.SQLServerException;
import dao.SinhVienDAO;
import model.nconnguoi.SinhVien;
import java.time.LocalDate;

// Import static nếu bạn để hàm này ở class khác
import static database.KNDatabase.ketNoiDatabase;

public class Main {
    // Xóa bỏ <SinhVienDAO> ở đây
    public static void main(String[] args) {
        
        // Kiểm tra kết nối thử (tùy vào logic hàm của bạn)
        try {
            ketNoiDatabase();
        } catch (SQLServerException e) {
            throw new RuntimeException(e);
        }

        // 1. Tạo đối tượng sinh viên mới
        SinhVien sv = new SinhVien();
        sv.setMaSV("SV001");
        sv.setHoDem("Nguyễn Văn");
        sv.setTen("An");
        sv.setNgaySinh(LocalDate.of(2005, 5, 20));
        sv.setGioiTinh("Nam");
        sv.setNamNhapHoc(2023);
        sv.setMaLop("K68-CNTT"); 
        sv.setSdt("0912345678");
        sv.setEmail("an.nv@gmail.com");
        sv.setTrangThaiSV("Đang học");
        sv.setMaHuyen("H001"); 

        // 2. Gọi DAO để lưu vào SQL Server
        // Lúc này Java sẽ hiểu SinhVienDAO là lớp bạn đã định nghĩa
        SinhVienDAO dao = new SinhVienDAO();
        dao.insertSinhVien(sv);
    }
}