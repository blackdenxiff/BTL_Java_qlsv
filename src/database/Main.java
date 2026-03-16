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
    public static void main(String[] args) throws SQLServerException {
            ketNoiDatabase();
    }
}