package controller.security;

import dao.nhethongdao.NguoiDungDAO;
import model.nhethong.NguoiDung;
import model.nhethong.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class ChangePasswordController {
    @FXML private PasswordField oldPasswordField, newPasswordField, confirmPasswordField;
    @FXML private Label messageLabel;

    private NguoiDungDAO dao = new NguoiDungDAO();

    @FXML
    private void handleChangePassword() {
        String oldPass = oldPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        // 1. Lấy thông tin từ Session đã lưu lúc đăng nhập
        String currentId = UserSession.getUsername();
        String currentType = UserSession.getUserType();

        // 2. Kiểm tra mật khẩu cũ (dùng hàm checkLogin bạn đã có)
        if (!dao.checkLogin(currentId, oldPass)) {
            showError("Mật khẩu cũ không chính xác!");
            return;
        }

        if (!newPass.equals(confirm)) {
            showError("Xác nhận mật khẩu mới không khớp!");
            return;
        }

        // 3. TẠO ĐỐI TƯỢNG NGUOIDUNG ĐỂ TRUYỀN VÀO HÀM UPDATE
        NguoiDung nd = new NguoiDung();
        nd.setUsername(currentId);   // WHERE username = ?
        nd.setMatKhau(newPass);      // SET matKhau = ?
        nd.setUsertype(currentType); // SET usertype = ?

        // 🔥 QUAN TRỌNG: Gán lại mã để khớp với logic IF/ELSE trong hàm updateNguoiDung của bạn
        if ("SV".equals(currentType)) {
            nd.setMaSV(currentId); // SET maSV = ?, maNV = NULL
        }
        else if ("NV".equals(currentType)) {
            nd.setMaNV(currentId); // SET maSV = NULL, maNV = ?
        }
        else if ("AD".equals(currentType)) {
            // SET maSV = NULL, maNV = NULL (như code bạn viết)
        }

        // 4. GỌI HÀM UPDATE CỦA BẠN
        try {
            dao.updateNguoiDung(nd);

            messageLabel.setText("Đổi mật khẩu thành công!");
            messageLabel.setStyle("-fx-text-fill: #2ecc71;");

            // Xóa trắng các ô nhập
            oldPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } catch (Exception e) {
            showError("Lỗi khi cập nhật dữ liệu!");
        }
    }

    private void showError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: #e74c3c;");
    }
}