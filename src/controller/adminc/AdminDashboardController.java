package controller.adminc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.util.Objects;

public class AdminDashboardController {

    @FXML
    private StackPane contentArea; // Nơi sẽ chứa các giao diện con

    @FXML
    private void switchPage(ActionEvent event) {
        // Lấy text của nút vừa bấm để quyết định load file nào
        Button btn = (Button) event.getSource();
        String menuText = btn.getText();

        String fxmlFile = "";

        if (menuText.contains("1.")) fxmlFile = "view/adminview/AccountManager.fxml";
       // else if (menuText.contains("2.")) fxmlFile = "/view/SystemConfig.fxml";
        // ... thêm các điều kiện khác cho 3, 4, 5

        loadFXML(fxmlFile);
    }

    private void loadFXML(String fxmlFile) {
        try {
            // Bước quan trọng: Tải file FXML con và thay thế vào contentArea
            Parent fxml = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(fxml);
        } catch (IOException e) {
            System.err.println("Không tìm thấy file: " + fxmlFile);
        }
    }
}