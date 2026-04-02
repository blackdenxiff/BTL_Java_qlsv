package controller.adminc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.nhethong.UserSession;
import java.io.IOException;
import java.util.Objects;

public class AdminDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Button btnAccounts, btnSystem, btnTraining, btnFinance, btnStats, btnChangePassword;

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
}