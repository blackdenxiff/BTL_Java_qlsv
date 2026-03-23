package controller.security;

import dao.nhethongdao.NguoiDungDao;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    // Lưu ý: Đừng để @FXML trên đối tượng DAO nhé, vì nó không phải thành phần giao diện
    private NguoiDungDao nguoiDungDao = new NguoiDungDao();

    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        // 1. Kiểm tra rỗng trước khi gọi DB (tiết kiệm tài nguyên)
        if (user.isEmpty() || pass.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin!");
            shakeNode(usernameField);
            shakeNode(passwordField);
            return;
        }

        boolean isSuccess = nguoiDungDao.checkLogin(user, pass);

        if (isSuccess) {

            messageLabel.setText("Đăng nhập thành công! Đang chuyển hướng...");
            messageLabel.setStyle("-fx-text-fill: #2ecc71;"); // Màu xanh lá hiện đại
            // Logic chuyển màn hình ở đây
        } else {
            showError("Tài khoản hoặc mật khẩu không chính xác!");
            shakeNode(usernameField);
            shakeNode(passwordField);
        }
    }

    // Hàm hiển thị lỗi gọn gàng
    private void showError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: #e74c3c;"); // Màu đỏ hiện đại
    }

    // Hàm tạo hiệu ứng rung khi sai
    private void shakeNode(javafx.scene.Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.play();
    }
}