package view.main;

import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.awt.*;
import java.awt.desktop.SystemEventListener;
import java.util.logging.Logger;

public class MainController {

    @FXML
    private TextField clock;

    @FXML
    private Button btnStop;

    @FXML
    private Button btnKillPower;

    @FXML
    private Button btnLaunch;

    @FXML
    private Label stripeLabel;

    @FXML
    private Label rpmflLabel;

    @FXML
    private Label rpmfrLabel;

    @FXML
    private Label rpmblLabel;

    @FXML
    private Gauge gaugeDistance;

    @FXML
    private Gauge gaugeAccel;

    @FXML
    Gauge gaugeVelocity;

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
        server = new Server(this);
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

    public void setVelocityLabel(double velocity){
        gaugeVelocity.setValue(velocity);
    }

    public void setDistanceLabel(double  distance){
        gaugeDistance.setValue(distance);
    }

    public void setAccelerationLabel(double  accel){
        gaugeAccel.setValue(accel);
    }

    public void setStripeLabel(String stripe){

        Platform.runLater(new Runnable() {
            @Override public void run() {
                stripeLabel.setText(stripe);
            }
        });

    }

    public void setRpmflLabel(String rpmfl){

        Platform.runLater(new Runnable() {
            @Override public void run() {
                rpmflLabel.setText(rpmfl);
            }
        });

    }

    public void setRpmfrLabel(String rpmfr){

        Platform.runLater(new Runnable() {
            @Override public void run() {
                rpmfrLabel.setText(rpmfr);
            }
        });

    }

    public void setRpmblLabel(String rpmbl){

        Platform.runLater(new Runnable() {
            @Override public void run() {
                rpmblLabel.setText(rpmbl);
            }
        });

    }

}
