package view.main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.*;

public class Server extends Thread {

    private static final int PORT = 5695;
    private static final int SPACE_X_PORT = 4445;
    public static final int ACK_FROM_SERVER = 0;
    private static final String DATA_REGEX = "^[0-9]+$";

    int distance, velocity, acceleration,
            rpm_fl, rpm_fr, rpm_br, rpm_bl,
            hp_volt, hp_temp, hp_volt1, hp_temp1,
            torque_fr, torque_fl, torque_br, torque_bl, imu1, imu2, imu3, imu4, imu5, imu6, imu7, imu8;
    int state = 0;
    String data, cmdString, readingString;

    private ServerSocket serverSocket;
    private Socket podSocket;
    private PrintWriter printWriter;
    private Scanner scanner;
    private PrintStream loggerStream;

    private MainController mainController;
    private byte status = 1, team_id = 2;
    private boolean isTimerRunning = false;

    private static Logger LOGGER;
    private static Handler loggerHandler = null;
    private static HashMap cmdHashMap;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        LOGGER = Logger.getLogger(Server.class.getName());

        try {
            loggerHandler = new FileHandler("logger.log");
            SimpleFormatter simple = new SimpleFormatter();
            loggerHandler.setFormatter(simple);
            LOGGER.addHandler(loggerHandler);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to set up logger.");
            e.printStackTrace();
        }

        cmdHashMap = new HashMap();
        cmdHashMap.put("CMD01", "distance");
        cmdHashMap.put("CMD02", "velocity");
        cmdHashMap.put("CMD03", "acceleration");
        cmdHashMap.put("CMD04", "stripe count");
        cmdHashMap.put("CMD05", "rpm fl");
        cmdHashMap.put("CMD06", "rpm fr");
        cmdHashMap.put("CMD07", "rpm bl");
        cmdHashMap.put("CMD08", "rpm br");
        cmdHashMap.put("CMD09", "state");
        cmdHashMap.put("CMD10", "hp volt");
        cmdHashMap.put("CMD11", "hp temp");
        cmdHashMap.put("CMD12", "hp volt1");
        cmdHashMap.put("CMD13", "hp temp1");
        cmdHashMap.put("CMD14", "torque fr");
        cmdHashMap.put("CMD15", "torque fl");
        cmdHashMap.put("CMD16", "torque br");
        cmdHashMap.put("CMD17", "torque bl");
        cmdHashMap.put("CMD18", "imu1");
        cmdHashMap.put("CMD19", "imu2");
        cmdHashMap.put("CMD20", "imu3");
        cmdHashMap.put("CMD21", "imu4");
        cmdHashMap.put("CMD22", "imu5");
        cmdHashMap.put("CMD23", "imu6");
        cmdHashMap.put("CMD24", "imu7");
        cmdHashMap.put("CMD25", "imu8");
    }

    public Server(MainController controller) {
        try {
            serverSocket = new ServerSocket(PORT);
            this.mainController = controller;
            LOGGER.log(Level.INFO, "Server initialised; Controller assigned.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Initialisation of Server failed.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        LOGGER.log(Level.INFO, "Awaiting connection from pod...");

        try {
            podSocket = serverSocket.accept();
            scanner = new Scanner(podSocket.getInputStream());
            printWriter = new PrintWriter(podSocket.getOutputStream());
            sendToPod(ACK_FROM_SERVER);
            LOGGER.log(Level.INFO, "Accepted connection from client.");
            startCommunication();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "run() failed. Closing scanner and printwriter.");
            e.printStackTrace();
        } finally {
            isTimerRunning = false;
            scanner.close();
            printWriter.close();
        }
    }

    private int parseData(String cmdString, String readingString) {
        int result = 0;

        if (!readingString.matches(DATA_REGEX)) {
            LOGGER.log(Level.INFO, String.format("%s: nan", cmdHashMap.get(cmdString)));
        } else {
            result = (int) Double.parseDouble(readingString);
            LOGGER.log(Level.INFO, String.format("%s: %d", cmdHashMap.get(cmdString), result));
        }

        return result;
    }

    private void startTimer(long startTime) {
        Thread timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.log(Level.INFO, "Timer started.");
                while (isTimerRunning) {
                    mainController.setClock((int) ((System.currentTimeMillis() - startTime) / 1000.0));
                }
            }
        });

        timerThread.start();
    }

    private void updateGauges() {
        Thread gaugeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Timeline gaugeLag = new Timeline(new KeyFrame(Duration.seconds(0.2), new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        mainController.setDistanceMeter(distance);
                        mainController.setGaugeVelocity(velocity);
                        mainController.setGaugeAcceleration(acceleration);
                        mainController.setGaugeRpmfl(rpm_fl);
                        mainController.setGaugeRpmfr(rpm_fr);
                        mainController.setGaugeRpmbl(rpm_bl);
                        mainController.setGaugeRpmbr(rpm_br);
                        mainController.setGaugeVoltage(hp_volt);
                        mainController.setGaugeTemp(hp_temp);
                        mainController.setGaugeVoltage1(hp_volt1);
                        mainController.setGaugeTemp1(hp_temp1);
                        mainController.setGaugeTorquefr(torque_fr);
                        mainController.setGaugeTorquefl(torque_fl);
                        mainController.setGaugeTorquebr(torque_br);
                        mainController.setGaugeTorquebl(torque_bl);
                    }
                }));
                gaugeLag.setCycleCount(Timeline.INDEFINITE);
                gaugeLag.play();
            }
        });

        gaugeThread.start();
    }

    private void setState(int state) {
        switch(state){
            case 0:
                mainController.setStateLabel("IDLE");
                break;
            case 1:
                mainController.setStateLabel("CALIBRATING");
                break;
            case 2:
                mainController.setStateLabel("READY");
                break;
            case 3:
                if (!isTimerRunning) {
                    startTimer(System.currentTimeMillis());
                    isTimerRunning = true;
                }
                mainController.setStateLabel("ACCELERATING");
                break;
            case 4:
                mainController.setStateLabel("DECELERATING");
                break;
            case 5:
                mainController.setBrakeIndicator();
                mainController.setStateLabel("EMERGENCY BRAKING");
                break;
            case 6:
                isTimerRunning = false;
                mainController.setStateLabel("RUN COMPLETE");
                break;
            case 7:
                mainController.setStateLabel("FAILURE STOPPED");
                break;
            case 8:
                mainController.setStateLabel("EXITING");
                break;
            case 9:
                mainController.setStateLabel("FINISHED");
                break;
            case 10:
                mainController.setStateLabel("INVALID");
                break;
            default:
                LOGGER.log(Level.WARNING, "Should never reach here.");
                break;
        }
    }

    private void startCommunication() {
        LOGGER.log(Level.INFO, "Communication between pod and base-station started.");

        if (podSocket == null) {
            LOGGER.log(Level.SEVERE, "NO POD SOCKET. Should never reach here.");
        }

        mainController.setTelemetryIndicator();
        updateGauges();

        while (scanner.hasNext()) {
            data = scanner.nextLine();
            cmdString = data.substring(0, 5);
            readingString = data.substring(5);

            switch (data.substring(0, 5)) {
                case "CMD01":
                    distance = parseData(cmdString, readingString);
                    break;
                case "CMD02":
                    velocity = parseData(cmdString, readingString);
                    break;
                case "CMD03":
                    acceleration = parseData(cmdString, readingString);
                    break;
                case "CMD04":
                    break;
                case "CMD05":
                    rpm_fl = parseData(cmdString, readingString);
                    break;
                case "CMD06":
                    rpm_fr = parseData(cmdString, readingString);
                    break;
                case "CMD07":
                    rpm_bl = parseData(cmdString, readingString);
                    break;
                case "CMD08":
                    rpm_br = parseData(cmdString, readingString);
                    break;
                case "CMD09":
                    state = parseData(cmdString, readingString);
                    setState(state);
                    break;
                case "CMD10":
                    hp_volt = parseData(cmdString, readingString);
                    break;
                case "CMD11":
                    hp_temp = parseData(cmdString, readingString);
                    break;
                case "CMD12":
                    hp_volt1 = parseData(cmdString, readingString);
                    break;
                case "CMD13":
                    hp_temp1 = parseData(cmdString, readingString);
                    break;
                case "CMD14":
                    torque_fr = parseData(cmdString, readingString);
                    break;
                case "CMD15":
                    torque_fl = parseData(cmdString, readingString);
                    break;
                case "CMD16":
                    torque_br = parseData(cmdString, readingString);
                    break;
                case "CMD17":
                    torque_bl = parseData(cmdString, readingString);
                    break;
                case "CMD18":
                    imu1 = parseData(cmdString, readingString);
                    break;
                case "CMD19":
                    imu2 = parseData(cmdString, readingString);
                    break;
                case "CMD20":
                    imu3 = parseData(cmdString, readingString);
                    break;
                case "CMD21":
                    imu4 = parseData(cmdString, readingString);
                    break;
                case "CMD22":
                    imu5 = parseData(cmdString, readingString);
                    break;
                case "CMD23":
                    imu6 = parseData(cmdString, readingString);
                    break;
                case "CMD24":
                    imu7 = parseData(cmdString, readingString);
                    break;
                case "CMD25":
                    imu8 = parseData(cmdString, readingString);
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Should never reach here.");
                    break;
            }

            //sendToSpaceX(status, team_id, acceleration, distance, velocity);
        }
    }

    public void sendToPod(int message) {
        if (podSocket == null) {
            LOGGER.log(Level.SEVERE, "NO POD SOCKET. Should never reach here.");
        }

        LOGGER.log(Level.INFO, String.format("Sending %s to pod", message));
        printWriter.println(message);
        printWriter.flush();
    }

    public static void sendToSpaceX(byte status, byte team_id,
                                    int acceleration, int distance, int velocity) {
        try {
            DatagramSocket spaceXSocket = new DatagramSocket();
            ByteBuffer buf = ByteBuffer.allocate(34); // BigEndian by default
            buf.put(team_id);
            buf.put(status);
            buf.putInt(acceleration);
            buf.putInt(distance);
            buf.putInt(velocity);
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(0);
            InetAddress IP =  InetAddress.getByName(/*_spaceXIP*/"localhost");
            DatagramPacket packet = new DatagramPacket(buf.array(), buf.limit(), IP, SPACE_X_PORT);
            spaceXSocket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
