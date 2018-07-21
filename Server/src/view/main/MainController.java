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
    private static final Paint indicatorOffColor = Paint.valueOf("#bd200c");
    private static final Paint indicatorInitColor = Paint.valueOf("#798dad");
    private static HashMap<Boolean, Color> colorHashMap = new HashMap<Boolean, Color>();
    private final Server server;
    private Thread serverThread;

    static {
        colorHashMap.put(true, dangerColor);
        colorHashMap.put(false, safeColor);
    }

    @FXML
    private Lcd clock;
    // Module stuff
    @FXML private Circle batIndicator, navIndicator, senIndicator, mtrIndicator;
    @FXML private Button btnStop, btnLaunch, btnRestart;
    @FXML private Button btnServicePropulsionStop, btnServicePropulsionGo;
    // Navigation stuff
    @FXML private JFXSlider distanceMeter;
    @FXML private Gauge gaugeAccel, gaugeVelocity;
    // RPM
    @FXML private Gauge gaugeRpmfl, gaugeRpmfr, gaugeRpmbl, gaugeRpmbr;
    // State
    @FXML private Label stateLabel;
    // Sensor stuff
    @FXML private Circle imuIndicator, imuIndicator1, imuIndicator2, imuIndicator3;
//    @FXML private Circle fproxiIndicator, fproxiIndicator1, fproxiIndicator2, fproxiIndicator3,
//            fproxiIndicator4, fproxiIndicator5, fproxiIndicator6, fproxiIndicator7;
//    @FXML private Circle rproxiIndicator, rproxiIndicator1, rproxiIndicator2, rproxiIndicator3,
//            rproxiIndicator4, rproxiIndicator5, rproxiIndicator6, rproxiIndicator7;
    @FXML private Circle frontBrakeIndicator, rearBrakeIndicator;
    @FXML private Label frontBrakeLabel, rearBrakeLabel;
    // HP battery
    @FXML private Gauge gaugeHpBattery;
    @FXML private Gauge gaugeVoltage, hpGaugeCurrent, gaugeTemp;
    @FXML private Lcd lowestVoltageGauge, highestVoltageGauge;
    // HP battery 1
    @FXML private Gauge gaugeHpBattery1;
    @FXML private Gauge gaugeVoltage1, hpGaugeCurrent1, gaugeTemp1;
    @FXML private Lcd lowestVoltageGauge1, highestVoltageGauge1;
    // LP battery
    @FXML private Gauge gaugeLpBattery;
    @FXML private Gauge lpVoltageGauge, lpCurrentGauge;
    // LP battery 1
    @FXML private Gauge gaugeLpBattery1;
    @FXML private Gauge lpVoltageGauge1, lpCurrentGauge1;
    // Telemetry stuff
    @FXML private Circle telemetryIndicator;
    @FXML private Label updatesLabel;
    @FXML private JFXTextField trackLengthTextField;
    @FXML private Label warningLabel;
    @FXML private Lcd distanceLabel;
    @FXML private Button trackLengthBtn;

    private int old_batModStatus, old_navModStatus, old_senModStatus, old_mtrModStatus,
            old_distance, old_velocity, old_acceleration,
            old_rpm_fl, old_rpm_fr, old_rpm_br, old_rpm_bl,
            old_hp_volt, old_hp_temp, old_hp_charge, old_hp_volt1,
            old_hp_temp1, old_hp_charge1, old_lp_charge, old_lp_charge1,
            old_hp_current, old_hp_current1, old_lowest_cell, old_highest_cell, old_lowest_cell1, old_highest_cell1,
            old_lp_voltage, old_lp_voltage1, old_lp_current, old_lp_current1;
    private int[] old_imu = new int[4];
//    private int[] old_proxi_front = new int[8];
//    private int[] old_proxi_rear = new int[8];
    private int[] old_em_brakes = new int[2];

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

    // ----------------------------------------------------------------------------------
    // Telemetry stuff
    // ----------------------------------------------------------------------------------
    public void setUpdatesLabel(String message){
        Platform.runLater(new Runnable() {
            @Override public void run() {
                updatesLabel.setText(message);
            }
        });
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

    public void enableBtnLaunch() {
        btnLaunch.setDisable(false);
    }

    public void disableBtnLaunch() {
        btnLaunch.setDisable(true);
    }

//    public void enableBtnStop() {
//        btnStop.setDisable(false);
//    }
//
//    public void disableBtnStop() {
//        btnStop.setDisable(true);
//    }

    public void enableServicePropulsion() {
        btnServicePropulsionGo.setDisable(false);
        btnServicePropulsionStop.setDisable(false);
    }

    public void disableServicePropulsion() {
        btnServicePropulsionGo.setDisable(true);
        btnServicePropulsionStop.setDisable(true);
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

    public void setClock(double time) {
        clock.setValue(time);
    }

    public void setTelemetryIndicatorOn() {
        telemetryIndicator.setFill(indicatorOnColor);
    }

    public void setTelemetryIndicatorOff(){
        telemetryIndicator.setFill(indicatorOffColor);
    }

    // ----------------------------------------------------------------------------------
    // Comms data stuff
    // ----------------------------------------------------------------------------------

    public void setBatIndicator(int bat) {
        if (old_batModStatus == bat) return;
        switch (bat) {
            case 0: batIndicator.setFill(indicatorInitColor); break;
            case 1: batIndicator.setFill(indicatorOnColor); break;
            case 2: batIndicator.setFill(dangerColor); break;
            default: break;
        }
        old_batModStatus = bat;
    }

    public void setNavIndicator(int nav) {
        if (old_navModStatus == nav) return;
        switch (nav) {
            case 0: navIndicator.setFill(indicatorInitColor); break;
            case 1: navIndicator.setFill(indicatorOnColor); break;
            case 2: navIndicator.setFill(dangerColor); break;
            default: break;
        }
        old_navModStatus = nav;
    }

    public void setSenIndicator(int sen) {
        if (old_senModStatus == sen) return;
        switch (sen) {
            case 0: senIndicator.setFill(indicatorInitColor); break;
            case 1: senIndicator.setFill(indicatorOnColor); break;
            case 2: senIndicator.setFill(dangerColor); break;
            default: break;
        }
        old_senModStatus = sen;
    }

    public void setMtrIndicator(int mtr) {
        if (old_mtrModStatus == mtr) return;
        switch (mtr) {
            case 0: mtrIndicator.setFill(indicatorInitColor); break;
            case 1: mtrIndicator.setFill(indicatorOnColor); break;
            case 2: mtrIndicator.setFill(dangerColor); break;
            default: break;
        }
        old_mtrModStatus = mtr;
    }

    public void setDistanceMeter(int distance) {
        if (old_distance == distance) return;
        distanceMeter.setValue(distance);
        distanceLabel.setValue(distance);
        old_distance = distance;
    }

    public void setGaugeVelocity(int velocity, boolean isDanger) {
        if (old_velocity == velocity) return;
        gaugeVelocity.setValue(velocity);
        gaugeVelocity.setValueColor(colorHashMap.get(isDanger));
        old_velocity = velocity;
    }

    public void setGaugeAcceleration(int accel, boolean isDanger) {
        if (old_acceleration == accel) return;
        gaugeAccel.setValue(accel);
        gaugeAccel.setValueColor(colorHashMap.get(isDanger));
        old_acceleration = accel;
    }

    public void setStateLabel(String state) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                stateLabel.setText(state);
            }
        });
    }

    public void setGaugeRpmfl(int rpm_fl, boolean isDanger) {
        if (old_rpm_fl == rpm_fl) return;
        gaugeRpmfl.setValue(rpm_fl);
        gaugeRpmfl.setValueColor(colorHashMap.get(isDanger));
        old_rpm_fl = rpm_fl;
    }

    public void setGaugeRpmfr(int rpm_fr, boolean isDanger) {
        if (old_rpm_fr == rpm_fr) return;
        gaugeRpmfr.setValue(rpm_fr);
        gaugeRpmfr.setValueColor(colorHashMap.get(isDanger));
        old_rpm_fr = rpm_fr;
    }

    public void setGaugeRpmbl(int rpm_bl, boolean isDanger) {
        if (old_rpm_bl == rpm_bl) return;
        gaugeRpmbl.setValue(rpm_bl);
        gaugeRpmbl.setValueColor(colorHashMap.get(isDanger));
        old_rpm_bl = rpm_bl;
    }

    public void setGaugeRpmbr(int rpm_br, boolean isDanger) {
        if (old_rpm_br == rpm_br) return;
        gaugeRpmbr.setValue(rpm_br);
        gaugeRpmbr.setValueColor(colorHashMap.get(isDanger));
        old_rpm_br = rpm_br;
    }

    public void setImuIndicator(int[] imu) {
//        if (Arrays.equals(old_imu, imu)) return;
        if (imu[0] == 1) {
            imuIndicator.setFill(indicatorOnColor);
        } else {
            imuIndicator.setFill(indicatorOffColor);
        }
        if (imu[1] == 1) {
            imuIndicator1.setFill(indicatorOnColor);
        } else {
            imuIndicator1.setFill(indicatorOffColor);
        }
        if (imu[2] == 1) {
            imuIndicator2.setFill(indicatorOnColor);
        } else {
            imuIndicator2.setFill(indicatorOffColor);
        }
        if (imu[3] == 1) {
            imuIndicator3.setFill(indicatorOnColor);
        } else {
            imuIndicator3.setFill(indicatorOffColor);
        }
//        old_imu = imu;
    }

    /*
    public void setProxi_FrontIndicator(int[] proxi_front) {
//        if (Arrays.equals(old_proxi_front, proxi_front)) return;
        if (proxi_front[0] == 1) {
            fproxiIndicator.setFill(indicatorOnColor);
        } else {
            fproxiIndicator.setFill(indicatorOffColor);
        }
        if (proxi_front[1] == 1) {
            fproxiIndicator1.setFill(indicatorOnColor);
        } else {
            fproxiIndicator1.setFill(indicatorOffColor);
        }
        if (proxi_front[2] == 1) {
            fproxiIndicator2.setFill(indicatorOnColor);
        } else {
            fproxiIndicator2.setFill(indicatorOffColor);
        }
        if (proxi_front[3] == 1) {
            fproxiIndicator3.setFill(indicatorOnColor);
        } else {
            fproxiIndicator3.setFill(indicatorOffColor);
        }
        if (proxi_front[4] == 1) {
            fproxiIndicator4.setFill(indicatorOnColor);
        } else {
            fproxiIndicator4.setFill(indicatorOffColor);
        }
        if (proxi_front[5] == 1) {
            fproxiIndicator5.setFill(indicatorOnColor);
        } else {
            fproxiIndicator5.setFill(indicatorOffColor);
        }
        if (proxi_front[6] == 1) {
            fproxiIndicator6.setFill(indicatorOnColor);
        } else {
            fproxiIndicator6.setFill(indicatorOffColor);
        }
        if (proxi_front[7] == 1) {
            fproxiIndicator7.setFill(indicatorOnColor);
        } else {
            fproxiIndicator7.setFill(indicatorOffColor);
        }
//        old_proxi_front = proxi_front;
    }

    public void setProxi_RearIndicator(int[] proxi_rear) {
//        if (Arrays.equals(old_proxi_rear, proxi_rear)) return;
        if (proxi_rear[0] == 1) {
            rproxiIndicator.setFill(indicatorOnColor);
        } else {
            rproxiIndicator.setFill(indicatorOffColor);
        }
        if (proxi_rear[1] == 1) {
            rproxiIndicator1.setFill(indicatorOnColor);
        } else {
            rproxiIndicator1.setFill(indicatorOffColor);
        }
        if (proxi_rear[2] == 1) {
            rproxiIndicator2.setFill(indicatorOnColor);
        } else {
            rproxiIndicator2.setFill(indicatorOffColor);
        }
        if (proxi_rear[3] == 1) {
            rproxiIndicator3.setFill(indicatorOnColor);
        } else {
            rproxiIndicator3.setFill(indicatorOffColor);
        }
        if (proxi_rear[4] == 1) {
            rproxiIndicator4.setFill(indicatorOnColor);
        } else {
            rproxiIndicator4.setFill(indicatorOffColor);
        }
        if (proxi_rear[5] == 1) {
            rproxiIndicator5.setFill(indicatorOnColor);
        } else {
            rproxiIndicator5.setFill(indicatorOffColor);
        }
        if (proxi_rear[6] == 1) {
            rproxiIndicator6.setFill(indicatorOnColor);
        } else {
            rproxiIndicator6.setFill(indicatorOffColor);
        }
        if (proxi_rear[7] == 1) {
            rproxiIndicator7.setFill(indicatorOnColor);
        } else {
            rproxiIndicator7.setFill(indicatorOffColor);
        }
//        old_proxi_rear = proxi_rear;
    }*/

    public void setBrakeIndicator(int[] em_brakes) {
//        if (Arrays.equals(old_em_brakes, em_brakes)) return;
        if (em_brakes[0] == 1) {
            frontBrakeIndicator.setFill(indicatorOnColor);
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    frontBrakeLabel.setText("DEPLOYED");
                }
            });
        } else {
            frontBrakeIndicator.setFill(indicatorOffColor);
        }
        if (em_brakes[1] == 1) {
            rearBrakeIndicator.setFill(indicatorOnColor);
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    rearBrakeLabel.setText("DEPLOYED");
                }
            });
        } else {
            rearBrakeIndicator.setFill(indicatorOffColor);
        }
//        old_em_brakes = em_brakes;
    }

    public void setGaugeVoltage(int hpVoltage, boolean isDanger) {
        if (old_hp_volt == hpVoltage) return;
        gaugeVoltage.setValue(hpVoltage);
        gaugeVoltage.setValueColor(colorHashMap.get(isDanger));
        old_hp_volt = hpVoltage;
    }

    public void setGaugeHpCurrent(int hpCurrent, boolean isDanger) {
        if (old_hp_current == hpCurrent) return;
        hpGaugeCurrent.setValue(hpCurrent);
        hpGaugeCurrent.setValueColor(colorHashMap.get(isDanger));
        old_hp_current = hpCurrent;
    }

    public void setGaugeHpBattery(int hpCharge, boolean isDanger) {
        if (old_hp_charge == hpCharge) return;
        gaugeHpBattery.setValue(hpCharge * 100);
        gaugeHpBattery.setValueColor(colorHashMap.get(isDanger));
        old_hp_charge = hpCharge;
    }

    public void setGaugeTemp(int hpTemp, boolean isDanger) {
        if (old_hp_temp == hpTemp) return;
        gaugeTemp.setValue(hpTemp);
        gaugeTemp.setValueColor(colorHashMap.get(isDanger));
        old_hp_temp = hpTemp;
    }

    public void setLowCell(int hpLowCell){
        if (old_lowest_cell == hpLowCell) return;
        lowestVoltageGauge.setValue(hpLowCell / 1000.0);
        old_lowest_cell = hpLowCell;
    }

    public void setHighCell(int hpHighCell){
        if (old_highest_cell == hpHighCell) return;
        highestVoltageGauge.setValue(hpHighCell / 1000.0);
        old_highest_cell = hpHighCell;
    }

    public void setGaugeVoltage1(int hpVoltage1, boolean isDanger) {
        if (old_hp_volt1 == hpVoltage1)
        gaugeVoltage1.setValue(hpVoltage1);
        gaugeVoltage1.setValueColor(colorHashMap.get(isDanger));
        old_hp_volt1 = hpVoltage1;
    }

    public void setGaugeHpCurrent1(int hpCurrent1, boolean isDanger) {
        if (old_hp_current1 == hpCurrent1)
        hpGaugeCurrent1.setValue(hpCurrent1);
        hpGaugeCurrent1.setValueColor(colorHashMap.get(isDanger));
        old_hp_current1 = hpCurrent1;
    }

    public void setGaugeHpBattery1(int hpCharge1, boolean isDanger) {
        if (old_hp_charge1 == hpCharge1) return;
        gaugeHpBattery1.setValue(hpCharge1 * 100);
        gaugeHpBattery1.setValueColor(colorHashMap.get(isDanger));
        old_hp_charge1 = hpCharge1;
    }

    public void setGaugeTemp1(int hpTemp1, boolean isDanger) {
        if (old_hp_temp1 == hpTemp1) return;
        gaugeTemp1.setValue(hpTemp1);
        gaugeTemp1.setValueColor(colorHashMap.get(isDanger));
        old_hp_temp1 = hpTemp1;
    }

    public void setLowCell1(int lowCell1){
        if (old_lowest_cell1 == lowCell1) return;
        lowestVoltageGauge1.setValue(lowCell1 / 1000.0);
        old_lowest_cell1 = lowCell1;
    }

    public void setHighCell1(int highCell1){
        if (old_highest_cell1 == highCell1) return;
        highestVoltageGauge1.setValue(highCell1 / 1000.0);
        old_highest_cell1 = highCell1;
    }

    public void setGaugeLpVoltage(int lpVoltage, boolean isDanger) {
        if (old_lp_voltage == lpVoltage) return;
        lpVoltageGauge.setValue(lpVoltage);
        lpVoltageGauge.setValueColor(colorHashMap.get(isDanger));
        old_lp_voltage = lpVoltage;
    }

    public void setGaugeLpCurrent(int lpCurrent, boolean isDanger) {
        if (old_lp_current == lpCurrent) return;
        lpCurrentGauge.setValue(lpCurrent);
        lpCurrentGauge.setValueColor(colorHashMap.get(isDanger));
        old_lp_current = lpCurrent;
    }

    public void setGaugeLpBattery(int lpCharge, boolean isDanger) {
        if (old_lp_charge == lpCharge) return;
        gaugeLpBattery.setValue(lpCharge * 100);
        gaugeLpBattery.setValueColor(colorHashMap.get(isDanger));
        old_lp_charge = lpCharge;
    }

    public void setGaugeLpVoltage1(int lpVoltage1, boolean isDanger) {
        if (old_lp_voltage1 == lpVoltage1) return;
        lpVoltageGauge1.setValue(lpVoltage1);
        lpVoltageGauge1.setValueColor(colorHashMap.get(isDanger));
        old_lp_voltage1 = lpVoltage1;
    }

    public void setGaugeLpCurrent1(int lpCurrent1, boolean isDanger) {
        if (old_lp_current1 == lpCurrent1) return;
        lpCurrentGauge1.setValue(lpCurrent1);
        lpCurrentGauge1.setValueColor(colorHashMap.get(isDanger));
        old_lp_current1 = lpCurrent1;
    }

    public void setGaugeLpBattery1(int lpCharge1, boolean isDanger) {
        if (old_lp_charge1 == lpCharge1) return;
        gaugeLpBattery1.setValue(lpCharge1 * 100);
        gaugeLpBattery1.setValueColor(colorHashMap.get(isDanger));
        old_lp_charge1 = lpCharge1;
    }
}
