package view.main;

import com.jfoenix.controls.JFXSlider;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MainController {

    @FXML
    private Gauge clock;

    @FXML
    private Button btnStop;

    @FXML
    private Button btnLaunch;

    @FXML
    private Button btnReset;

    @FXML
    private Gauge gaugeAccel;

    @FXML
    private Gauge gaugeVelocity;

    @FXML
    private Gauge gaugeTemp;

    @FXML
    private Gauge gaugeVoltage;

    @FXML
    private Gauge gaugeTemp1;

    @FXML
    private Gauge gaugeVoltage1;

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
    private Circle telemetryIndicator;

    @FXML
    private Circle rightBrakeIndicator;

    @FXML
    private Circle leftBrakeIndicator;

//    @FXML
//    private TextArea logger;

    @FXML
    public void initialize() {
        System.out.println("Called MainController.initialize");
        btnStop.setOnAction(event -> this.handleBtnStop());
        btnLaunch.setOnAction(event -> this.handleBtnLaunch());
        btnReset.setOnAction(event -> this.handleBtnReset());
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

    private void handleBtnReset() {
        server.sendToPod(3);
    }

    public void setGaugeVelocity(int velocity) {
        gaugeVelocity.setValue(velocity);
    }

    public void setGaugeAcceleration(int accel) {
        gaugeAccel.setValue(accel);
    }

    public void setGaugeTemp(int temp) {
        gaugeTemp.setValue(temp);
    }

    public void setGaugeVoltage(int voltage) {
        gaugeVoltage.setValue(voltage);
    }

    public void setGaugeTemp1(int temp) {
        gaugeTemp1.setValue(temp);
    }

    public void setGaugeVoltage1(int voltage) {
        gaugeVoltage1.setValue(voltage);
    }

    public void setGaugeRpmfl(int rpmfl) {
        gaugeRpmfl.setValue(rpmfl);
    }

    public void setGaugeRpmfr(int rpmfr) {
        gaugeRpmfr.setValue(rpmfr);
    }

    public void setGaugeRpmbl(int rpmbl) {
        gaugeRpmbl.setValue(rpmbl);
    }

    public void setGaugeRpmbr(int rpmbr) {
        gaugeRpmbr.setValue(rpmbr);
    }

    public void setClock(int time) {
        clock.setValue(time);
    }

    public void setDistanceMeter(int distance) {
        distanceMeter.setValue(distance);
    }

    public void setTelemetryIndicator() {
        telemetryIndicator.setFill(javafx.scene.paint.Color.YELLOW);
    }

    public void setBrakeIndicator() {
        leftBrakeIndicator.setFill(javafx.scene.paint.Color.YELLOW);
        rightBrakeIndicator.setFill(javafx.scene.paint.Color.YELLOW);
    }

//    public void setLogger(String logMessage) {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                logger.setText(logMessage);
//            }
//        });
//    }
}
