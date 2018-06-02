package view.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main3.fxml"));
        primaryStage.setTitle("HYPED Mission Control System");
        primaryStage.setScene(new Scene(root, 1236, 666));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
