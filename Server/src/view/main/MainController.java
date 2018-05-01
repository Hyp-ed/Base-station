package view.main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.logging.Logger;

public class MainController {

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getSimpleName());

    @FXML
    private Button btnStop;

    @FXML
    private Button btnKillPower;

    @FXML
    private Button btnLaunch;

    @FXML
    private Label velocityLabel;

    @FXML
    public void initialize() {
        System.out.println("Called MainController.initialize");
        btnStop.setOnAction(event -> this.handleBtnStop());

        btnKillPower.setOnAction(event -> this.handleBtnKillPower());

        btnLaunch.setOnAction(event -> this.handleBtnLaunch());
    }

//    private final Server server;
//
//    public MainController() {
//        System.out.println("Called MainController.MainController");
//        server = new Server();
//        server.start();
//        Platform.runLater(() -> printVelocity(server.distance));
//
//    }
//
    private void handleBtnStop() {
        System.out.println("STOP");
    }



    private void handleBtnKillPower() {
        //server.sendToPod(2);
        System.out.println("KILL");
    }



    private void handleBtnLaunch() {
        //server.sendToPod(3);
        System.out.println("LAUNCH");
    }

    public void printVelocity(double a) {
        velocityLabel.setText(Double.toString(a));
    }

}
