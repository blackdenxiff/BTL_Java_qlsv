package controller.nhanvien;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.Accordion;
import javafx.scene.layout.VBox;

import java.net.URL;

public class HomeNVController {

    @FXML
    private VBox contentArea;

    @FXML
    private Accordion menuAccordion;

    @FXML
    private BarChart<String, Number> chart;

    // ================= INIT =================
    @FXML
    public void initialize() {

        // Mở menu đầu tiên
        if (menuAccordion != null && !menuAccordion.getPanes().isEmpty()) {
            menuAccordion.setExpandedPane(menuAccordion.getPanes().get(0));
        }

        // Load trang chủ mặc định
        loadHome();
    }

    // ================= HOME =================
    @FXML
    public void handleHome() {
        loadHome();
    }

    private void loadHome() {
        loadView("HomeContent.fxml"); // file dashboard riêng
    }
    @FXML
    public void handleLogout() {
        try {
            System.out.println("CLICK LOGOUT"); // test

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/securityview/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ================= MENU =================
    @FXML
    private void handleSinhVien() {
        loadView("UpdateSinhVien.fxml");
    }

    @FXML
    private void handleNhanVien() {
        loadView("UpdateNhanVien.fxml");
    }

    @FXML
    private void handleLopHocPhan() {
        loadView("LopHocPhan.fxml");
    }

    @FXML
    public void handleNhapDiem() {
        loadView("NhapDiem.fxml");
    }

    @FXML
    private void handleHocPhi() {
        loadView("HocPhi.fxml");
    }

    // ================= LOAD VIEW =================
    private void loadView(String fileName) {
        try {
            String path = "/view/nhanvienview/" + fileName;

            System.out.println("👉 Loading: " + path);

            URL url = getClass().getResource(path);

            if (url == null) {
                System.out.println("KHÔNG TÌM THẤY FILE: " + path);
                return;
            }

            Parent view = FXMLLoader.load(url);

            // THAY NỘI DUNG (KHÔNG ĐỔI SCENE)
            contentArea.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}