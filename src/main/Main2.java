package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class  Main2 extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Khởi chạy màn hình Đăng nhập đầu tiên
            // Đường dẫn tính từ thư mục resources/src
            java.net.URL fxmlLocation = getClass().getResource("/view/securityview/Login.fxml");

            if (fxmlLocation == null) {
                // Kiểm tra xem bạn để Login.fxml ở /view/ hay /view/securityview/
                fxmlLocation = getClass().getResource("/view/Login.fxml");
            }

            if (fxmlLocation == null) {
                System.err.println("LỖI: Không tìm thấy file Login.fxml! Kiểm tra lại thư mục view.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // 2. Thiết lập Scene
            Scene scene = new Scene(root, 500, 700);

            // Nạp CSS cho màn hình đăng nhập nếu cần
            // scene.getStylesheets().add(getClass().getResource("/view/LoginCSS.css").toExternalForm());

            primaryStage.setTitle("Hệ thống Quản lý Sinh viên - Đăng nhập");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Màn hình login thường không cho resize
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khởi chạy hệ thống: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}