package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Xác định đường dẫn file FXML (Ưu tiên cấu trúc thư mục của bạn)
            String fxmlPath = "/view/securityview/Login.fxml";
            URL fxmlLocation = getClass().getResource(fxmlPath);

            // Dự phòng nếu bạn để file ở ngoài thư mục view trực tiếp
            if (fxmlLocation == null) {
                fxmlPath = "/view/Login.fxml";
                fxmlLocation = getClass().getResource(fxmlPath);
            }

            // Kiểm tra cuối cùng trước khi load
            if (fxmlLocation == null) {
                System.err.println(" LỖI NGHIÊM TRỌNG: Không tìm thấy file Login.fxml!");
                System.err.println("Vị trí đã tìm: /view/securityview/ và /view/");
                return;
            }

            System.out.println(" Đang nạp giao diện từ: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // 2. Thiết lập Scene (Kích thước 500x700 cho màn hình Login)
            Scene scene = new Scene(root, 600, 700);

            // 3. Cấu hình Stage (Cửa sổ chính)
            primaryStage.setTitle("Hệ thống Quản lý - Đăng nhập");
            primaryStage.setScene(scene);

            // Giữ cho giao diện ổn định, không bị vỡ layout khi kéo dãn
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();

            primaryStage.show();
            System.out.println(" Hệ thống đã sẵn sàng.");

        } catch (Exception e) {
            System.err.println(" Lỗi khởi chạy hệ thống:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Khởi chạy ứng dụng JavaFX
        launch(args);
    }
}