package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        Button btn = new Button("Hello JavaFX");
        Scene scene = new Scene(btn, 300, 200);

        stage.setScene(scene);
        stage.setTitle("Test");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}