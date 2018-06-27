package view.main;

import com.jfoenix.controls.JFXSlider;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

public class MainController {

    @FXML
    private Gauge clock;

    @FXML
    private Button btnStop;

    @FXML
    private Button btnLaunch;

    @FXML
    private Button btnServicePropulsionStop;

    @FXML
    private Button btnServicePropulsionGo;

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
    private Gauge gaugeTorquefr;

    @FXML
    private Gauge gaugeTorquefl;

    @FXML
    private Gauge gaugeTorquebr;

    @FXML
    private Gauge gaugeTorquebl;

    @FXML
    private Gauge lpBattery;

    @FXML
    private Gauge lpBattery1;

    @FXML
    private Gauge hpBattery;

    @FXML
    private Gauge hpBattery1;

    @FXML
    private JFXSlider distanceMeter;

    @FXML
    private Circle telemetryIndicator;

    @FXML
    private Circle rightBrakeIndicator;

    @FXML
    private Circle leftBrakeIndicator;

    @FXML
    private Label stateLabel;

    @FXML
    private Button trackLengthBtn;

    @FXML
    public void initialize() {
        System.out.println("Called MainController.initialize");
        btnStop.setOnAction(event -> this.handleBtnStop());
        btnLaunch.setOnAction(event -> this.handleBtnLaunch());
        trackLengthBtn.setOnAction(event -> this.handleTrackLengthBtn());
        btnServicePropulsionGo.setOnAction(event -> this.handleBtnServicePropulsionGo());
        btnServicePropulsionStop.setOnAction(event -> this.handleBtnServicePropulsionStop());
    }

    private final Server server;

    public MainController() {
        System.out.println("Called MainController.MainController");
        server = new Server(this);
        server.start();
    }

    private void handleBtnStop() { server.sendToPod(1); }

    private void handleBtnLaunch() {
        server.sendToPod(2);
    }

    private void handleBtnServicePropulsionGo() {
        server.sendToPod(5);
    }

    private void handleBtnServicePropulsionStop() {
        server.sendToPod(6);
    }

    private void handleTrackLengthBtn() {
        //TODO: get value from text area
        server.sendToPod(4);
        server.sendToPod(10000); //change to entered value
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

    public void setGaugeTorquefr(int torquefr) { gaugeTorquefr.setValue(torquefr); }

    public void setGaugeTorquebl(int torquebl) {
        gaugeTorquefl.setValue(torquebl);
    }

    public void setGaugeTorquebr(int torquebr) {
        gaugeTorquebr.setValue(torquebr);
    }

    public void setGaugeTorquefl(int torquefl) { gaugeTorquebl.setValue(torquefl); }

    public void setGaugeLpbattery(int lpcharge) {
        lpBattery.setValue(lpcharge);
    }

    public void setGaugeLpbattery1(int lpcharge1) {
        lpBattery1.setValue(lpcharge1);
    }

    public void setGaugeHpbattery(int hpcharge) {
        hpBattery.setValue(hpcharge);
    }

    public void setGaugeHpbattery1(int hpcharge1) {
        hpBattery1.setValue(hpcharge1);
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

    public void setStateLabel(String state) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                stateLabel.setText(state);
            }
        });

    }

    public void enableBtnLaunch(){
        btnLaunch.setDisable(false);
    }

    public void enableBtnStop(){
        btnStop.setDisable(false);
    }

    public void enableServicePropulsion(){
        btnServicePropulsionGo.setDisable(false);
        btnServicePropulsionStop.setDisable(false);
    }
}
