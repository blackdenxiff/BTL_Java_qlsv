package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Mọi ứng dụng JavaFX đều phải kế thừa lớp Application
public class MainTestLogin extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tải cấu trúc UI từ tệp login.fxml
        // Lưu ý: Đảm bảo đường dẫn file fxml chính xác với cấu trúc thư mục của bạn
        java.net.URL fxmlLocation = getClass().getResource("/view/securityview/Login.fxml");
        Parent root = null;
        if (fxmlLocation == null) {
            System.err.println("LỖI: Không tìm thấy file FXML! Hãy kiểm tra lại đường dẫn.");
        } else {
            root = FXMLLoader.load(fxmlLocation);
            // ... tiếp tục các dòng code khác
        }

        // Đặt tiêu đề cho cửa sổ ứng dụng
        primaryStage.setTitle("Ứng dụng Quản lý");

        // Tạo một Scene (cảnh) chứa giao diện vừa tải, đặt kích thước mặc định là 350x250 pixel
        primaryStage.setScene(new Scene(root, 500, 700));

        // Cố định kích thước cửa sổ, không cho phép kéo giãn
        primaryStage.setResizable(false);

        // Hiển thị cửa sổ lên màn hình
        primaryStage.show();
    }

    // Hàm main tiêu chuẩn của Java, dùng để khởi chạy phương thức launch() của JavaFX
    public static void main(String[] args) {
        launch(args);
    }
}