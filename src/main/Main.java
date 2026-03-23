package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Tải file FXML
            // Lưu ý: Đảm bảo file đặt tại: src/main/resources/view/security/LoginView.fxml
            // Hoặc sửa lại đường dẫn dưới đây cho đúng với thư mục thực tế của bạn
            java.net.URL fxmlLocation = getClass().getResource("/view/Login.fxml");

            if (fxmlLocation == null) {
                System.err.println("LỖI: Không tìm thấy file FXML! Hãy kiểm tra lại thư mục src/main/resources.");
                return;
            }

            Parent root = FXMLLoader.load(fxmlLocation);

            // 2. Tạo Scene
            // Theo giao diện mẫu, kích thước 800x600 hoặc 900x650 là phù hợp nhất để hiện nền xanh rộng
            Scene scene = new Scene(root, 900, 650);

            // 3. Nạp file CSS (Quan trọng để có bo góc và màu sắc như ảnh)
            // Đảm bảo file style.css nằm cùng thư mục với file FXML
            String css = getClass().getResource("/view/LoginCSS.css").toExternalForm();
            scene.getStylesheets().add(css);

            // 4. Thiết lập Stage (Cửa sổ)
            primaryStage.setTitle("Hệ thống Quản lý Đào tạo - UTC");

            // Thêm icon ứng dụng nếu bạn có file ảnh (tùy chọn)
            // primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo_utc.png")));

            primaryStage.setScene(scene);

            // Cố định kích thước để giao diện không bị vỡ khi kéo giãn
            primaryStage.setResizable(false);

            // Đưa cửa sổ ra giữa màn hình
            primaryStage.centerOnScreen();

            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Lỗi khởi động ứng dụng JavaFX: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Khởi chạy ứng dụng
        launch(args);
    }
}