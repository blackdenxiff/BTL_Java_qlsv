package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main_nv extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/nhanvienview/MainNhanVien.fxml")
            );

            Scene scene = new Scene(loader.load());

            stage.setTitle("Quản lý nhân viên");
            stage.setScene(scene);
            stage.setWidth(1100);
            stage.setHeight(700);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}