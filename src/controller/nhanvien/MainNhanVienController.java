package controller.nhanvien;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class MainNhanVienController {

    @FXML
    private StackPane contentArea;

    // ===== Hàm load màn =====
    private void loadUI(String path) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(path));
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== GIẢNG DẠY =====
    @FXML
    void dangKyLop() {
        loadUI("/view/nhanvienview/giangday/DangKy.fxml");
    }

    @FXML
    void quanLyLop() {
        loadUI("/view/nhanvienview/giangday/Lop.fxml");
    }

    @FXML
    void nhapDiem() {
        loadUI("/view/nhanvienview/giangday/NhapDiem.fxml");
    }

    @FXML
    void suaDiem() {
        loadUI("/view/nhanvienview/giangday/SuaDiem.fxml");
    }

    // ===== SINH VIÊN =====
    @FXML
    void danhSachSV() {
        loadUI("/view/nhanvienview/sinhvien/List.fxml");
    }

    @FXML
    void traCuuSV() {
        loadUI("/view/nhanvienview/sinhvien/Search.fxml");
    }

    @FXML
    void ketQua() {
        loadUI("/view/nhanvienview/sinhvien/KetQua.fxml");
    }

    // ===== CỐ VẤN =====
    @FXML
    void lopHC() {
        loadUI("/view/nhanvienview/covan/LopHC.fxml");
    }

    @FXML
    void tienDo() {
        loadUI("/view/nhanvienview/covan/TienDo.fxml");
    }

    @FXML
    void canhBao() {
        loadUI("/view/nhanvienview/covan/CanhBao.fxml");
    }

    // ===== LỊCH =====
    @FXML
    void lichDay() {
        loadUI("/view/nhanvienview/lich/LichDay.fxml");
    }

    @FXML
    void lichThi() {
        loadUI("/view/nhanvienview/lich/LichThi.fxml");
    }

    // ===== THÔNG TIN =====
    @FXML
    void hoSo() {
        loadUI("/view/nhanvienview/thongtin/HoSo.fxml");
    }

    @FXML
    void dsNhanVien() {
        loadUI("/view/nhanvienview/thongtin/NhanVien.fxml");
    }

    // ===== ĐĂNG XUẤT =====
    @FXML
    void logout() {
        loadUI("/view/securityview/Login.fxml");
    }
}