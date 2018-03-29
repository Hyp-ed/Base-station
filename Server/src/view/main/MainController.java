package view.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

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
    public void initialize() {
        System.out.println("Called MainController.initialize");
        btnStop.setOnAction(event -> this.handleBtnStop());

        btnKillPower.setOnAction(event -> this.handleBtnKillPower());

        btnLaunch.setOnAction(event -> this.handleBtnLaunch());
    }

    private final Server server;

    public MainController() {
        System.out.println("Called MainController.MainController");
        server = new Server();
        server.start();
    }

    private void handleBtnStop() {
        server.sendToPod(1);
    }



    private void handleBtnKillPower() {
        server.sendToPod(2);
    }



    private void handleBtnLaunch() {
        server.sendToPod(3);
    }

}
