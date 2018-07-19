package view.main;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;

/**
 * MainController class
 *
 * @author Kofi and Isa, HYPED 17/18
 */
public class MainController {

    private static final Color safeColor = Color.WHITE;
    private static final Color dangerColor = Color.RED;
    private static final Paint indicatorOnColor = Paint.valueOf("#199a30");
    private static final Paint inidcatorOffColor = Paint.valueOf("#bd200c");
    private static HashMap<Boolean, Color> colorHashMap = new HashMap<Boolean, Color>();
    private final Server server;
    private Thread serverThread;

    static {
        colorHashMap.put(true, dangerColor);
        colorHashMap.put(false, safeColor);
    }

    @FXML
    private Lcd clock;

    @FXML
    private Button btnStop;

    @FXML
    private Button btnLaunch;

    @FXML
    private Button btnServicePropulsionStop;

    @FXML
    private Button btnServicePropulsionGo;

    @FXML
    private Button btnRestart;

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
    private Circle frontBrakeIndicator;

    @FXML
    private Circle rearBrakeIndicator;

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
    private Label updatesLabel;

    @FXML
    private JFXTextField trackLengthTextField;

    @FXML
    private Label warningLabel;

    @FXML
    private Label frontBrakeLabel;

    @FXML
    private Label rearBrakeLabel;

    @FXML
    private Label distanceLabel;

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
        btnRestart.setOnAction(event -> this.handleBtnRestart());
    }

    public MainController() {
        System.out.println("Called MainController.MainController");
        server = new Server(this);
        serverThread = new Thread(server);
        serverThread.start();
    }

    private void resetAll() {
        server.resetAll();
    }

    private void handleBtnRestart() {
        if (server.isCommunicating()) {
            System.out.println("NOT ALLOWED TO RESTART WHEN CLIENT IS CONNECTED");
            return;
        }

        serverThread.interrupt();
        resetAll();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        serverThread = new Thread(server);
        serverThread.start();
    }

    private void handleBtnStop() {
        server.sendToPod(1);
        setUpdatesLabel("Send STOP.");
    }

    private void handleBtnLaunch() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("verification.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("HYPED Mission Control System");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root));
        stage.show();
        stage.setOnHiding( event -> {
            letsGo();
        } );
    }

    public void letsGo() {
        server.sendToPod(2);
        setUpdatesLabel("Send LAUNCH.");
    }

    private void handleBtnServicePropulsionGo() {
        server.sendToPod(5);
        setUpdatesLabel("Send SERVICE PROPULSION GO.");
    }

    private void handleBtnServicePropulsionStop() {
        server.sendToPod(6);
        setUpdatesLabel("Send SERVICE PROPULSION STOP.");
    }

    private void trackLengthWarningOn() {
        warningLabel.setOpacity(1);
    }

    public void trackLengthWarningOff() {
        warningLabel.setOpacity(0);
    }

    private void handleTrackLengthBtn() {
        int trackLength = Integer.parseInt(trackLengthTextField.getText());

        if (trackLength > 0 && trackLength < 1250) {
            server.sendToPod(4);
            server.sendToPod(trackLength);  //change to entered value
            setUpdatesLabel("Send track length " + trackLength + "m to pod.");
            trackLengthWarningOff();
        } else {
            trackLengthWarningOn();
        }

        trackLengthTextField.setText("");
    }

    public void setDistanceMeter(int distance) {
        distanceMeter.setValue(distance);
    }

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

    public void setGaugeLpbattery(int lpCharge, boolean isDanger) {
        gaugeLpBattery.setValue(lpCharge * 100);
        gaugeLpBattery.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeLpbattery1(int lpCharge1, boolean isDanger) {
        gaugeLpBattery1.setValue(lpCharge1 * 100);
        gaugeLpBattery1.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeHpBattery(int hpCharge, boolean isDanger) {
        gaugeHpBattery.setValue(hpCharge * 100);
        gaugeHpBattery.setValueColor(colorHashMap.get(isDanger));
    }

    public void setGaugeHpBattery1(int hpCharge1, boolean isDanger) {
        gaugeHpBattery1.setValue(hpCharge1 * 100);
        gaugeHpBattery1.setValueColor(colorHashMap.get(isDanger));
    }

    public void setClock(double time) {
        clock.setValue(time);
    }

    public void setTelemetryIndicatorOn() {
        telemetryIndicator.setFill(indicatorOnColor);
    }

    public void setTelemetryIndicatorOff(){
        telemetryIndicator.setFill(inidcatorOffColor);
    }

    public void setBrakeIndicator(int em_brakes[]) {
        if (em_brakes[0] == 1) {
            frontBrakeIndicator.setFill(indicatorOnColor);
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    frontBrakeLabel.setText("DEPLOYED");
                }
            });
        } else {
            frontBrakeIndicator.setFill(inidcatorOffColor);
        }
        if (em_brakes[1] == 1) {
            rearBrakeIndicator.setFill(indicatorOnColor);
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    rearBrakeLabel.setText("DEPLOYED");
                }
            });
        } else {
            rearBrakeIndicator.setFill(inidcatorOffColor);
        }
    }

    public void setStateLabel(String state) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                stateLabel.setText(state);
            }
        });
    }

    public void setImuIndicator(int imu[]) {
        if (imu[0] == 1) {
            imuIndicator.setFill(indicatorOnColor);
        } else {
            imuIndicator.setFill(inidcatorOffColor);
        }
        if (imu[1] == 1) {
            imuIndicator1.setFill(indicatorOnColor);
        } else {
            imuIndicator1.setFill(inidcatorOffColor);
        }
        if (imu[2] == 1) {
            imuIndicator2.setFill(indicatorOnColor);
        } else {
            imuIndicator2.setFill(inidcatorOffColor);
        }
        if (imu[3] == 1) {
            imuIndicator3.setFill(indicatorOnColor);
        } else {
            imuIndicator3.setFill(inidcatorOffColor);
        }
    }

    public void setProxi_FrontIndicator(int proxi_front[]) {
        if (proxi_front[0] == 1) {
            fproxiIndicator.setFill(indicatorOnColor);
        } else {
            fproxiIndicator.setFill(inidcatorOffColor);
        }
        if (proxi_front[1] == 1) {
            fproxiIndicator1.setFill(indicatorOnColor);
        } else {
            fproxiIndicator1.setFill(inidcatorOffColor);
        }
        if (proxi_front[2] == 1) {
            fproxiIndicator2.setFill(indicatorOnColor);
        } else {
            fproxiIndicator2.setFill(inidcatorOffColor);
        }
        if (proxi_front[3] == 1) {
            fproxiIndicator3.setFill(indicatorOnColor);
        } else {
            fproxiIndicator3.setFill(inidcatorOffColor);
        }
        if (proxi_front[4] == 1) {
            fproxiIndicator4.setFill(indicatorOnColor);
        } else {
            fproxiIndicator4.setFill(inidcatorOffColor);
        }
        if (proxi_front[5] == 1) {
            fproxiIndicator5.setFill(indicatorOnColor);
        } else {
            fproxiIndicator5.setFill(inidcatorOffColor);
        }
        if (proxi_front[6] == 1) {
            fproxiIndicator6.setFill(indicatorOnColor);
        } else {
            fproxiIndicator6.setFill(inidcatorOffColor);
        }
        if (proxi_front[7] == 1) {
            fproxiIndicator7.setFill(indicatorOnColor);
        } else {
            fproxiIndicator7.setFill(inidcatorOffColor);
        }
    }


    public void setProxi_RearIndicator(int proxi_rear[]) {
        if (proxi_rear[0] == 1) {
            rproxiIndicator.setFill(indicatorOnColor);
        } else {
            rproxiIndicator.setFill(inidcatorOffColor);
        }
        if (proxi_rear[1] == 1) {
            rproxiIndicator1.setFill(indicatorOnColor);
        } else {
            rproxiIndicator1.setFill(inidcatorOffColor);
        }
        if (proxi_rear[2] == 1) {
            rproxiIndicator2.setFill(indicatorOnColor);
        } else {
            rproxiIndicator2.setFill(inidcatorOffColor);
        }
        if (proxi_rear[3] == 1) {
            rproxiIndicator3.setFill(indicatorOnColor);
        } else {
            rproxiIndicator3.setFill(inidcatorOffColor);
        }
        if (proxi_rear[4] == 1) {
            rproxiIndicator4.setFill(indicatorOnColor);
        } else {
            rproxiIndicator4.setFill(inidcatorOffColor);
        }
        if (proxi_rear[5] == 1) {
            rproxiIndicator5.setFill(indicatorOnColor);
        } else {
            rproxiIndicator5.setFill(inidcatorOffColor);
        }
        if (proxi_rear[6] == 1) {
            rproxiIndicator6.setFill(indicatorOnColor);
        } else {
            rproxiIndicator6.setFill(inidcatorOffColor);
        }
        if (proxi_rear[7] == 1) {
            rproxiIndicator7.setFill(indicatorOnColor);
        } else {
            rproxiIndicator7.setFill(inidcatorOffColor);
        }
    }

    public void enableBtnLaunch() {
        btnLaunch.setDisable(false);
    }

    public void disableBtnLaunch() {
        btnLaunch.setDisable(true);
    }

    public void enableBtnStop() {
        btnStop.setDisable(false);
    }

    public void disableBtnStop() {
        btnStop.setDisable(true);
    }

    public void enableServicePropulsion() {
        btnServicePropulsionGo.setDisable(false);
        btnServicePropulsionStop.setDisable(false);
    }

    public void disableServicePropulsion() {
        btnServicePropulsionGo.setDisable(true);
        btnServicePropulsionStop.setDisable(true);
    }

    public void setUpdatesLabel(String message){
        Platform.runLater(new Runnable() {
            @Override public void run() {
                updatesLabel.setText(message);
            }
        });

    }

    public void setDistanceLabel(int distance){
        Platform.runLater(new Runnable() {
            @Override public void run() {
                distanceLabel.setText(Integer.toString(distance) + "m");
            }
        });

    }
}
