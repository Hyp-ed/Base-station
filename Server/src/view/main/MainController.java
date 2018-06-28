package view.main;

import com.jfoenix.controls.JFXSlider;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MainController {

    private static final Color safeColor = Color.WHITE;
    private static final Color dangerColor = Color.RED;

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

    public void setDistanceMeter(int distance) {
        distanceMeter.setValue(distance);
    }

    // TODO(Isa): Refactor
    public void setGaugeVelocity(int velocity, boolean isDanger) {
        gaugeVelocity.setValue(velocity);

        if (isDanger) {
            gaugeVelocity.setValueColor(dangerColor);
        } else {
            gaugeVelocity.setValueColor(safeColor);
        }
    }

    public void setGaugeAcceleration(int accel, boolean isDanger) {
        gaugeAccel.setValue(accel);

        if (isDanger) {
            gaugeAccel.setValueColor(dangerColor);
        } else {
            gaugeAccel.setValueColor(safeColor);
        }
    }

    public void setGaugeTemp(int temp, boolean isDanger) {
        gaugeTemp.setValue(temp);

        if (isDanger) {
            gaugeTemp.setValueColor(dangerColor);
        } else {
            gaugeTemp.setValueColor(safeColor);
        }
    }

    public void setGaugeVoltage(int voltage, boolean isDanger) {
        gaugeVoltage.setValue(voltage);

        if (isDanger) {
            gaugeVoltage.setValueColor(dangerColor);
        } else {
            gaugeVoltage.setValueColor(safeColor);
        }
    }

    public void setGaugeTemp1(int temp1, boolean isDanger) {
        gaugeTemp1.setValue(temp1);

        if (isDanger) {
            gaugeTemp1.setValueColor(dangerColor);
        } else {
            gaugeTemp1.setValueColor(safeColor);
        }
    }

    public void setGaugeVoltage1(int voltage1, boolean isDanger) {
        gaugeVoltage1.setValue(voltage1);

        if (isDanger) {
            gaugeVoltage1.setValueColor(dangerColor);
        } else {
            gaugeVoltage1.setValueColor(safeColor);
        }
    }

    public void setGaugeRpmfl(int rpm_fl, boolean isDanger) {
        gaugeRpmfl.setValue(rpm_fl);

        if (isDanger) {
            gaugeRpmfl.setValueColor(dangerColor);
        } else {
            gaugeRpmfl.setValueColor(safeColor);
        }
    }

    public void setGaugeRpmfr(int rpm_fr, boolean isDanger) {
        gaugeRpmfr.setValue(rpm_fr);

        if (isDanger) {
            gaugeRpmfr.setValueColor(dangerColor);
        } else {
            gaugeRpmfr.setValueColor(safeColor);
        }
    }

    public void setGaugeRpmbl(int rpm_bl, boolean isDanger) {
        gaugeRpmbl.setValue(rpm_bl);

        if (isDanger) {
            gaugeRpmbl.setValueColor(dangerColor);
        } else {
            gaugeRpmbl.setValueColor(safeColor);
        }
    }

    public void setGaugeRpmbr(int rpm_br, boolean isDanger) {
        gaugeRpmbr.setValue(rpm_br);

        if (isDanger) {
            gaugeRpmbr.setValueColor(dangerColor);
        } else {
            gaugeRpmbr.setValueColor(safeColor);
        }
    }

    public void setGaugeTorquefr(int torque_fr, boolean isDanger) {
        gaugeTorquefr.setValue(torque_fr);

        if (isDanger) {
            gaugeTorquefr.setValueColor(dangerColor);
        } else {
            gaugeTorquefr.setValueColor(safeColor);
        }
    }

    public void setGaugeTorquebl(int torque_bl, boolean isDanger) {
        gaugeTorquefl.setValue(torque_bl);

        if (isDanger) {
            gaugeTorquefl.setValueColor(dangerColor);
        } else {
            gaugeTorquefl.setValueColor(safeColor);
        }
    }

    public void setGaugeTorquebr(int torque_br, boolean isDanger) {
        gaugeTorquebr.setValue(torque_br);

        if (isDanger) {
            gaugeTorquebr.setValueColor(dangerColor);
        } else {
            gaugeTorquebr.setValueColor(safeColor);
        }
    }

    public void setGaugeTorquefl(int torque_fl, boolean isDanger) {
        gaugeTorquebl.setValue(torque_fl);

        if (isDanger) {
            gaugeTorquebl.setValueColor(dangerColor);
        } else {
            gaugeTorquebl.setValueColor(safeColor);
        }
    }

    public void setGaugeLpbattery(int lpCharge, boolean isDanger) {
        gaugeLpBattery.setValue(lpCharge);

        if (isDanger) {
            gaugeLpBattery.setValueColor(dangerColor);
        } else {
            gaugeLpBattery.setValueColor(safeColor);
        }
    }

    public void setGaugeLpbattery1(int lpCharge1, boolean isDanger) {
        gaugeLpBattery1.setValue(lpCharge1);

        if (isDanger) {
            gaugeLpBattery1.setValueColor(dangerColor);
        } else {
            gaugeLpBattery1.setValueColor(safeColor);
        }
    }

    public void setGaugeHpBattery(int hpCharge, boolean isDanger) {
        gaugeHpBattery.setValue(hpCharge);

        if (isDanger) {
            gaugeHpBattery.setValueColor(dangerColor);
        } else {
            gaugeHpBattery.setValueColor(safeColor);
        }
    }

    public void setGaugeHpBattery1(int hpCharge1, boolean isDanger) {
        gaugeHpBattery1.setValue(hpCharge1);

        if (isDanger) {
            gaugeHpBattery1.setValueColor(dangerColor);
        } else {
            gaugeHpBattery1.setValueColor(safeColor);
        }
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
