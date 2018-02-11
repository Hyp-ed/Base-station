package view.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;

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

    private void handleBtnStop() {
        LOGGER.log(Level.INFO, "stop button called");
    }

    private void handleBtnReverse() {
        LOGGER.log(Level.INFO, "reverse button called");
    }

    private void handleBtnKillPower() {
        LOGGER.log(Level.INFO, "kill power button called");
    }

    private void handleBtnFoward() {
        LOGGER.log(Level.INFO, "forward button called");
    }

    private void handleBtnLaunch() {
        LOGGER.log(Level.INFO, "launch button called");
    }

}
