package view.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;

import java.util.logging.Logger;

public class MainController {

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getSimpleName());

    @FXML
    private Button btnStop;
    @FXML
    private Button btnReverse;
    @FXML
    private Button btnKillPower;
    @FXML
    private Button btnForward;
    @FXML
    private Button btnLaunch;

    @FXML
    private Slider progressBar;

    @FXML
    public void initialize() {
        System.out.println("Called MainController.initialize");
        btnStop.setOnAction(event -> this.handleBtnStop());
        btnReverse.setOnAction(event -> this.handleBtnReverse());
        btnKillPower.setOnAction(event -> this.handleBtnKillPower());
        btnForward.setOnAction(event -> this.handleBtnFoward());
        btnLaunch.setOnAction(event -> this.handleBtnLaunch());
    }

    private final Server server;

    public MainController() {
        System.out.println("Called MainController.MainController");
        server = new Server();
        server.start();
    }

    private void handleBtnStop() {
        server.sendToPod("STOP");
    }

    private void handleBtnReverse() {
        server.sendToPod("REVERSE");
    }

    private void handleBtnKillPower() {
        server.sendToPod("KILL POWER");
    }

    private void handleBtnFoward() {
        server.sendToPod("FORWARD");
    }

    private void handleBtnLaunch() {
        server.sendToPod("LAUNCH");
    }

}
