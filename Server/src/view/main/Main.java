package view.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    private Server servr;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));

        StackPane root = loader.load();

        MainController controller = loader.getController();

        servr = new Server();

        //listen for changes to speed property and updates velocity label
        servr.speedProperty().addListener((obs, oldSpeed, newSpeed) -> {

            // update controller on FX Application Thread:

            Platform.runLater(() -> controller.printVelocity(newSpeed.doubleValue()));

        });

        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    @Override

    public void stop() {

        if (servr != null) {

            servr.shutdown();

        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
