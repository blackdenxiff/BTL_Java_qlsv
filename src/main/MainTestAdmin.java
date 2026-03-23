package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainTestAdmin extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Chỉ định đường dẫn file FXML (Bắt đầu bằng / vì nó nằm trong resources/src)
            // Lưu ý: Tên file phải khớp hoàn toàn (Admin.fxml)
            java.net.URL fxmlLocation = getClass().getResource("/view/adminview/Admin.fxml");

            if (fxmlLocation == null) {
                System.err.println("LỖI: Không tìm thấy file Admin.fxml! Hãy kiểm tra thư mục view.");
                return;
            }

            // 2. Load giao diện
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // 3. Tạo Scene và nạp CSS (nếu file Admin.fxml chưa nạp CSS)
            Scene scene = new Scene(root);

            // Nếu bạn muốn nạp CSS trực tiếp từ code thay vì trong FXML:
            // scene.getStylesheets().add(getClass().getResource("/view/AdminCSS.css").toExternalForm());

            primaryStage.setTitle("Hệ thống Quản lý Sinh viên - Admin Panel");
            primaryStage.setScene(scene);

            // Cho phép hiển thị toàn màn hình hoặc kích thước mặc định
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khởi chạy: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}