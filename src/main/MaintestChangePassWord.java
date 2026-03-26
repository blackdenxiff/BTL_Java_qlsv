package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.nhethong.UserSession;

public class MaintestChangePassWord extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        UserSession.saveSession("admin2", "AD");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/securityview/ChangePassword.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 500, 650);

        primaryStage.setTitle("Thay doi mat khau");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}