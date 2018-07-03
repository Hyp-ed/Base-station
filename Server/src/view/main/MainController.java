package view.main;

import com.jfoenix.controls.JFXSlider;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class MainController {

    private static final Color safeColor = Color.WHITE;
    private static final Color dangerColor = Color.RED;
    private static HashMap<Boolean, Color> colorHashMap = new HashMap<Boolean, Color>();
    private final Server server;

    static {
        colorHashMap.put(true, dangerColor);
        colorHashMap.put(false, safeColor);
    }

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
    private Gauge gaugeLpBattery;

    @FXML
    private Gauge gaugeLpBattery1;

    @FXML
    private Gauge gaugeHpBattery;

    @FXML
    private Gauge gaugeHpBattery1;

    @FXML
    private JFXSlider distanceMeter;

    @FXML
    private Circle telemetryIndicator;

    @FXML
    private Circle rightBrakeIndicator;

    @FXML
    private Circle leftBrakeIndicator;

    @FXML
    private Circle imuIndicator;

    @FXML
    private Circle imuIndicator1;

    @FXML
    private Circle imuIndicator2;

    @FXML
    private Circle imuIndicator3;

    @FXML
    private Circle fproxiIndicator;

    @FXML
    private Circle fproxiIndicator1;

    @FXML
    private Circle fproxiIndicator2;

    @FXML
    private Circle fproxiIndicator3;

    @FXML
    private Circle fproxiIndicator4;

    @FXML
    private Circle fproxiIndicator5;

    @FXML
    private Circle fproxiIndicator6;

    @FXML
    private Circle fproxiIndicator7;

    @FXML
    private Circle rproxiIndicator;

    @FXML
    private Circle rproxiIndicator1;

    @FXML
    private Circle rproxiIndicator2;

    @FXML
    private Circle rproxiIndicator3;

    @FXML
    private Circle rproxiIndicator4;

    @FXML
    private Circle rproxiIndicator5;

    @FXML
    private Circle rproxiIndicator6;

    @FXML
    private Circle rproxiIndicator7;

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

    public MainController() {
        System.out.println("Called MainController.MainController");
        server = new Server(this);
        server.start();
    }

    private void handleBtnStop() { server.sendToPod(1); }

//    private void handleBtnLaunch() {
//        server.sendToPod(2);
//    }

    private void handleBtnLaunch() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("verification.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("HYPED Mission Control System");
        stage.setScene(new Scene(root));
        stage.show();
        stage.setOnHiding( event -> {letsGo();
        System.out.println("LETS GOOOOOOOOOOOOO");} );
    }

    public void letsGo() {
        server.sendToPod(2);
    }

    private void handleBtnServicePropulsionGo() {
        server.sendToPod(5);
    }

    private void handleBtnServicePropulsionStop() {
        server.sendToPod(6);
    }

    private void handleTrackLengthBtn() {
        // TODO(Kofi): get value from text area
        server.sendToPod(4);
        server.sendToPod(10000); //change to entered value
    }

    public void setDistanceMeter(int distance) {
        distanceMeter.setValue(distance);
    }

    // TODO(Isa): Refactor
    public void setGaugeVelocity(int velocity, boolean isDanger) {
        gaugeVelocity.setValue(velocity);
        gaugeVelocity.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeAcceleration(int accel, boolean isDanger) {
        gaugeAccel.setValue(accel);
        gaugeAccel.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeTemp(int temp, boolean isDanger) {
        gaugeTemp.setValue(temp);
        gaugeTemp.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeVoltage(int voltage, boolean isDanger) {
        gaugeVoltage.setValue(voltage);
        gaugeVoltage.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeTemp1(int temp1, boolean isDanger) {
        gaugeTemp1.setValue(temp1);
        gaugeTemp1.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeVoltage1(int voltage1, boolean isDanger) {
        gaugeVoltage1.setValue(voltage1);
        gaugeVoltage1.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeRpmfl(int rpm_fl, boolean isDanger) {
        gaugeRpmfl.setValue(rpm_fl);
        gaugeRpmfl.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeRpmfr(int rpm_fr, boolean isDanger) {
        gaugeRpmfr.setValue(rpm_fr);
        gaugeRpmfr.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeRpmbl(int rpm_bl, boolean isDanger) {
        gaugeRpmbl.setValue(rpm_bl);
        gaugeRpmbl.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeRpmbr(int rpm_br, boolean isDanger) {
        gaugeRpmbr.setValue(rpm_br);
        gaugeRpmbr.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeTorquefr(int torque_fr, boolean isDanger) {
        gaugeTorquefr.setValue(torque_fr);
        gaugeTorquefr.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeTorquebl(int torque_bl, boolean isDanger) {
        gaugeTorquefl.setValue(torque_bl);
        gaugeTorquefl.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeTorquebr(int torque_br, boolean isDanger) {
        gaugeTorquebr.setValue(torque_br);
        gaugeTorquebr.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeTorquefl(int torque_fl, boolean isDanger) {
        gaugeTorquebl.setValue(torque_fl);
        gaugeTorquebl.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeLpbattery(int lpCharge, boolean isDanger) {
        gaugeLpBattery.setValue(lpCharge);
        gaugeLpBattery.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeLpbattery1(int lpCharge1, boolean isDanger) {
        gaugeLpBattery1.setValue(lpCharge1);
        gaugeLpBattery1.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeHpBattery(int hpCharge, boolean isDanger) {
        gaugeHpBattery.setValue(hpCharge);
        gaugeHpBattery.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeHpBattery1(int hpCharge1, boolean isDanger) {
        gaugeHpBattery1.setValue(hpCharge1);
        gaugeHpBattery1.setValueColor(colorHashMap.get(isDanger));
    }

    public void setClock(int time) {
        clock.setValue(time);
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

    public void setImuIndicator(int imu[]) {
        if (imu[0]==1){
            imuIndicator.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            imuIndicator.setFill(Color.BLACK);
        }
        if (imu[1]==1){
            imuIndicator1.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            imuIndicator1.setFill(Color.BLACK);
        }
        if (imu[2]==1){
            imuIndicator2.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            imuIndicator2.setFill(Color.BLACK);
        }
        if (imu[3]==1){
            imuIndicator3.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            imuIndicator3.setFill(Color.BLACK);
        }
    }

    public void setProxi_FrontIndicator(int imu[]) {
        if (imu[0]==1){
            fproxiIndicator.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            fproxiIndicator.setFill(Color.BLACK);
        }
        if (imu[1]==1){
            fproxiIndicator1.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            fproxiIndicator1.setFill(Color.BLACK);
        }
        if (imu[2]==1){
            fproxiIndicator2.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            fproxiIndicator2.setFill(Color.BLACK);
        }
        if (imu[3]==1){
            fproxiIndicator3.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            fproxiIndicator3.setFill(Color.BLACK);
        }
        if (imu[4]==1){
            fproxiIndicator4.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            fproxiIndicator4.setFill(Color.BLACK);
        }
        if (imu[5]==1){
            fproxiIndicator5.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            fproxiIndicator5.setFill(Color.BLACK);
        }
        if (imu[6]==1){
            fproxiIndicator6.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            fproxiIndicator6.setFill(Color.BLACK);
        }
        if (imu[7]==1){
            fproxiIndicator7.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            fproxiIndicator7.setFill(Color.BLACK);
        }
    }


    public void setProxi_RearIndicator(int imu[]) {
        if (imu[0]==1){
            rproxiIndicator.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            rproxiIndicator.setFill(Color.BLACK);
        }
        if (imu[1]==1){
            rproxiIndicator1.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            rproxiIndicator1.setFill(Color.BLACK);
        }
        if (imu[2]==1){
            rproxiIndicator2.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            rproxiIndicator2.setFill(Color.BLACK);
        }
        if (imu[3]==1){
            rproxiIndicator3.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            rproxiIndicator3.setFill(Color.BLACK);
        }
        if (imu[4]==1){
            rproxiIndicator4.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            rproxiIndicator4.setFill(Color.BLACK);
        }
        if (imu[5]==1){
            rproxiIndicator5.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            rproxiIndicator5.setFill(Color.BLACK);
        }
        if (imu[6]==1){
            rproxiIndicator6.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            rproxiIndicator6.setFill(Color.BLACK);
        }
        if (imu[7]==1){
            rproxiIndicator7.setFill(javafx.scene.paint.Color.YELLOW);
        } else {
            rproxiIndicator7.setFill(Color.BLACK);
        }
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
