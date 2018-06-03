package view.main;

import com.jfoenix.controls.JFXSlider;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Gauge clock;

    @FXML
    private Button btnStop;

    @FXML
    private Button btnLaunch;

    @FXML
    private Gauge gaugeAccel;

    @FXML
    private Gauge gaugeVelocity;

    @FXML
    private Gauge gaugeRpmfl;

    @FXML
    private Gauge gaugeRpmfr;

    @FXML
    private Gauge gaugeRpmbl;

    @FXML
    private Gauge gaugeRpmbr;

    @FXML
    private JFXSlider distanceMeter;

    @FXML
    public void initialize() {
        System.out.println("Called MainController.initialize");
        btnStop.setOnAction(event -> this.handleBtnStop());
        btnLaunch.setOnAction(event -> this.handleBtnLaunch());
    }

    private final Server server;

    public MainController() {
        System.out.println("Called MainController.MainController");
        server = new Server(this);
        server.start();
    }

    private void handleBtnStop() {
        server.sendToPod(1);
    }

    private void handleBtnLaunch() {
        server.sendToPod(2);
    }

    public void setGaugeVelocity(int velocity){
        gaugeVelocity.setValue(velocity);
    }

    public void setGaugeAcceleration(int accel){
        gaugeAccel.setValue(accel);
    }

    public void setGaugeRpmfl(int rpmfl){
        gaugeRpmfl.setValue(rpmfl);
    }

    public void setGaugeRpmfr(int rpmfr){
        gaugeRpmfr.setValue(rpmfr);
    }

    public void setGaugeRpmbl(int rpmbl){
        gaugeRpmbl.setValue(rpmbl);
    }

    public void setGaugeRpmbr(int rpmbr){
        gaugeRpmbr.setValue(rpmbr);
    }

    public void setClock(int time){ clock.setValue(time); }

    public void setDistanceMeter(int distance){ distanceMeter.setValue(distance); }
}
