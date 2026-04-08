package controller.security;

import dao.nhethongdao.NguoiDungDAO; // Đảm bảo đúng tên Class DAO của bạn
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private NguoiDungDAO nguoiDungDao = new NguoiDungDAO();

    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin!");
            shakeNode(usernameField);
            shakeNode(passwordField);
            return;
        }

        // 1. Kiểm tra xem tài khoản có bị khóa không trước tiên
        if (nguoiDungDao.isAccountLocked(user)) {
            showError("Tài khoản của bạn đã bị khóa!");
            shakeNode(usernameField);
            return; // Dừng luôn, không cho đăng nhập
        }

        // 2. Nếu không bị khóa, tiến hành kiểm tra mật khẩu
        boolean isSuccess = nguoiDungDao.checkLogin(user, pass);

        if (isSuccess) {
            // Lấy loại người dùng để biết đi đâu (AD, NV, SV)
            String userType = nguoiDungDao.getUserType(user);
            model.nhethong.UserSession.saveSession(user, userType);
            messageLabel.setText("Đăng nhập thành công!");
            messageLabel.setStyle("-fx-text-fill: #2ecc71;");

            // Thực hiện chuyển màn hình
            navigateToDashboard(event, userType);
        } else {
            showError("Tài khoản hoặc mật khẩu không chính xác!");
            shakeNode(usernameField);
            shakeNode(passwordField);
        }
    }

    // HÀM QUAN TRỌNG: Điều hướng dựa trên vai trò
    private void navigateToDashboard(ActionEvent event, String userType) {
        String fxmlPath = "";
        String title = "";

        if (userType == null) return;

        switch (userType) {
            case "AD":
                fxmlPath = "/view/adminview/Admin.fxml";
                title = "Hệ thống Quản lý - Admin";
                break;
            case "NV": //chưa làm
                fxmlPath = "/view/nhanvienview/HomeNV.fxml";
                title = "Hệ thống Quản lý - Nhân viên";
                break;
            case "SV": // chưa lamf
                fxmlPath = "/view/sinhvienview/SinhVienDashboard.fxml";
                title = "Hệ thống Quản lý - Sinh viên";
                break;
            default:
                showError("Vai trò không hợp lệ!");
                return;
        }

        try {
            // Load file FXML mới
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Lấy Stage (cửa sổ) hiện tại từ nút bấm
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setResizable(true);

            // Thay thế Scene cũ bằng Scene mới
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen(); // Căn giữa lại cửa sổ vì kích thước Dashboard thường to hơn Login
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Lỗi hệ thống: Không thể nạp giao diện!");
        }
    }

    private void showError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void shakeNode(javafx.scene.Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.play();
    }
}