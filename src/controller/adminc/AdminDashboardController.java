package controller.adminc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.nhethong.UserSession;

import java.io.IOException;
import java.util.Objects;

public class AdminDashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    private void switchPage(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String menuText = btn.getText();
        String fxmlFile = "";

        // 1. Kiểm tra điều kiện trỏ đến các trang quản lý
        if (menuText.contains("1. Tài khoản & Phân quyền")) {
            fxmlFile = "/view/adminview/AccountManager.fxml";
        }
        // 2. Kiểm tra nếu nhấn nút "Đổi mật khẩu"
        else if (menuText.equalsIgnoreCase("Đổi mật khẩu")) {
            fxmlFile = "/view/securityview/ChangePassword.fxml";
        }

        if (!fxmlFile.isEmpty()) {
            loadFXML(fxmlFile);
        }
    }

    // Hàm xử lý Đăng xuất
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Xóa dữ liệu phiên đăng nhập
            UserSession.clear();

            // Lấy Stage hiện tại từ nút bấm và đóng nó lại
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            // Tải lại màn hình Login
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/securityview/Login.fxml")));
            Stage loginStage = new Stage();
            loginStage.setTitle("Đăng nhập hệ thống");
            loginStage.setScene(new Scene(root));
            loginStage.setResizable(false);
            loginStage.show();

        } catch (IOException e) {
            System.err.println("Lỗi khi quay lại màn hình Login: " + e.getMessage());
        }
    }

    private void loadFXML(String fxmlFile) {
        try {
            Parent fxml = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            contentArea.getChildren().setAll(fxml);
        } catch (IOException e) {
            System.err.println("Không tìm thấy file FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }
}