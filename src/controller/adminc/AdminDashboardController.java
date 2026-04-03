package controller.adminc;

import dao.nconnguoidao.NhanVienDAO;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.nconnguoi.NhanVien;
import model.nhethong.UserSession;
import java.io.IOException;
import java.util.Objects;

public class AdminDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Button btnAccounts, btnSystem, btnTraining, btnFinance, btnStats, btnChangePassword, btnManageSV, btnManageNV;;

    @FXML
    private void switchPage(ActionEvent event) {
        Object source = event.getSource();
        String fxmlFile = "";

        // Sửa lại đường dẫn chuẩn (bỏ /src)
        if (source == btnAccounts) {
            fxmlFile = "/view/adminview/AccountManager.fxml";
        } else if (source == btnSystem) {
            fxmlFile = "/view/adminview/SystemTrainingManager.fxml";
        } else if (source == btnFinance) {
            fxmlFile = "/view/adminview/FinanceManager.fxml";
        } else if (source == btnStats) {
            fxmlFile = "/view/adminview/ReportManager.fxml";
        } else if (source == btnChangePassword) {
            fxmlFile = "/view/securityview/ChangePassword.fxml";
        }
        else if (source == btnManageSV) {
            // Hệ thống sẽ mượn luôn file FXML và Controller cũ bên nhanvienview mà không bị lỗi
            fxmlFile = "/view/nhanvienview/UpdateSinhVien.fxml";
        } else if (source == btnManageNV) {
            fxmlFile = "/view/nhanvienview/UpdateNhanVien.fxml";
        }

        if (!fxmlFile.isEmpty()) {
            loadFXML(fxmlFile);
        }
    }

    private void loadFXML(String fxmlFile) {
        try {
            // Sử dụng FXMLLoader để có quyền kiểm soát tốt hơn
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Ép kích thước nội dung khớp với vùng trắng (StackPane)
            if (root instanceof Region) {
                ((Region) root).prefWidthProperty().bind(contentArea.widthProperty());
                ((Region) root).prefHeightProperty().bind(contentArea.heightProperty());
            }

            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            System.err.println("Không tìm thấy file: " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            UserSession.clear();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/securityview/Login.fxml")));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Trong class AdminDashboardController
    private NhanVienDAO nvDAO = new NhanVienDAO();

    @FXML
    private void handleShowProfile(javafx.scene.input.MouseEvent event) {
        String currentId = UserSession.getUsername();
        // Admin có thể có thông tin trong bảng NhanVien hoặc chỉ là tài khoản AD thuần túy
        NhanVien nv = nvDAO.getNhanVienByMa(currentId);

        String thongTin;
        if (nv != null) {
            thongTin = String.format(
                    "Mã Admin: %s\nHọ và Tên: %s %s\nNgày sinh: %s\nChức vụ: %s\nEmail: %s",
                    nv.getMaNV(), nv.getHoDem(), nv.getTen(), nv.getNgaySinh(), nv.getLoaiNV(), nv.getEmail()
            );
        } else {
            thongTin = "Tài khoản Quản trị hệ thống: " + currentId;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông tin tài khoản");
        alert.setHeaderText("Hồ sơ Admin");
        alert.setContentText(thongTin);
        alert.showAndWait();
    }
}