package view.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import view.Server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {

    private static final Logger LOGGER = Logger.getLogger(Controller.class.getSimpleName());

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
        btnStop.setOnAction(event -> this.handleBtnStop());
        btnReverse.setOnAction(event -> this.handleBtnReverse());
        btnKillPower.setOnAction(event -> this.handleBtnKillPower());
        btnForward.setOnAction(event -> this.handleBtnFoward());
        btnLaunch.setOnAction(event -> this.handleBtnLaunch());
    }

//when a button is pressed the corresponding function sends a command to the client
    private Server srvr = new Server();
    private void handleBtnStop() {
        try {
            srvr.sendToPod("STOP");
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, "stop button called");
    }

    private void handleBtnReverse() {
        try {
            srvr.sendToPod("REVERSE");
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, "reverse button called");
    }

    private void handleBtnKillPower() {
        try {
            srvr.sendToPod("KILLPOWER");
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, "kill power button called");
    }

    private void handleBtnFoward() {
        try {
            srvr.sendToPod("FORWARD");
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, "forward button called");
    }

    private void handleBtnLaunch() {
        try {
            srvr.sendToPod("LAUNCH");
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, "launch button called");
    }



}
