package controller.nhanvien;

import dao.quanlydao.DangKiDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class HocPhiController {

    private DangKiDAO dkDAO = new DangKiDAO();

    @FXML
    private TextField txtMaSV;

    @FXML
    private TextField txtDonGia;

    // ================= BUTTON =================
    @FXML
    private void handleTinhHocPhi() {
        try {
            String maSV = txtMaSV.getText().trim();
            double donGia = Double.parseDouble(txtDonGia.getText().trim());

            if (maSV.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập mã SV!");
                return;
            }

            int tongTinChi = dkDAO.tongTinChiSinhVienDangKy(maSV);

            if (tongTinChi == 0) {
                showAlert(Alert.AlertType.INFORMATION,
                        "Thông báo",
                        "Sinh viên chưa đăng ký môn học!");
                return;
            }

            double tongTien = tongTinChi * donGia;

            String content =
                    "Mã SV: " + maSV + "\n" +
                            "Tín chỉ: " + tongTinChi + "\n" +
                            "Tổng tiền: " + String.format("%,.0f", tongTien) + " VNĐ";

            showAlert(Alert.AlertType.INFORMATION, "Học phí", content);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đơn giá phải là số!");
        }
    }

    // ================= ALERT =================
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}