package view.main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.*;

public class Server implements Runnable {

    private static final int PORT = 5695;
    private static final String SPACE_X_IP = "localhost";
    private static final int SPACE_X_PORT = 4445;
    public static final int ACK_FROM_SERVER = 0;
    private static final String DATA_REGEX =  "^\\d*\\.?\\d+|\\d+\\.?\\d*$"; // "^[0-9]+$"

    int distance, velocity, acceleration,
            rpm_fl, rpm_fr, rpm_br, rpm_bl,
            hp_volt, hp_temp, hp_charge, hp_volt1, hp_temp1, hp_charge1, lp_charge, lp_charge1,
            imuReceived, proxi_frontReceived, proxi_rearReceived;
    int state = 0;
    int[] imu = new int[4];
    int[] proxi_front = new int[8];
    int[] proxi_rear = new int[8];

    // Danger flags, true if value exceeds threshold
    boolean dDistance, dVelocity, dAcceleration,
            dRpm_fl, dRpm_fr, dRpm_br, dRpm_bl,
            dHp_volt, dHp_temp, dHp_charge, dHp_volt1, dHp_temp1, dHp_charge1, dLp_charge, dLp_charge1;
//    boolean dState = false;

    String data, cmdString, readingString;

    private ServerSocket serverSocket;
    private Socket podSocket;
    private InetAddress spaceXAddress;
    DatagramPacket spaceXPacket;
    DatagramSocket spaceXSocket;
    ByteBuffer spaceXBuffer = ByteBuffer.allocate(34); // BigEndian by default
    private PrintWriter printWriter;
    private Scanner scanner;

    private MainController mainController;
    private byte status = 1, team_id = 2;
    private boolean isTimerRunning = false;
    private boolean isCommunicating = false;

    private static Logger LOGGER;
    private static Handler loggerHandler = null;

    static {
        // Set up Logger
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        LOGGER = Logger.getLogger(Server.class.getName());

        try {
            loggerHandler = new FileHandler("logger.log");
            SimpleFormatter simple = new SimpleFormatter();
            loggerHandler.setFormatter(simple);
            LOGGER.addHandler(loggerHandler);
            LOGGER.setLevel(Level.WARNING);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to set up logger.");
            e.printStackTrace();
        }
    }

    public Server(MainController controller) {
        try {
            serverSocket = new ServerSocket(PORT);
            spaceXSocket = new DatagramSocket();
            spaceXAddress = InetAddress.getByName(SPACE_X_IP);
            this.mainController = controller;
            LOGGER.log(Level.INFO, "Server initialised; Controller assigned.");
        } catch (UnknownHostException e) {
            LOGGER.log(Level.SEVERE, "Connection to SpaceX Server failed.");
        } catch (SocketException e) {
            LOGGER.log(Level.SEVERE, "Initialisation of Socket failed.");
            e.printStackTrace();
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
            LOGGER.log(Level.INFO, "Communication finished.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "run() failed.");
            e.printStackTrace();
        } finally {
            LOGGER.log(Level.INFO, "Communication terminated. Closing socket, scanner and printwriter.");
            isTimerRunning = false;
            try {
                podSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            scanner.close();
            printWriter.close();
        }
    }

    public boolean isCommunicating() {
        return isCommunicating;
    }

    public void sendToPod(int message) {
        if (podSocket == null) {
            LOGGER.log(Level.SEVERE, "NO POD SOCKET. Should never reach here.");
        }

        LOGGER.log(Level.INFO, String.format("Sending %s to pod", message));
        printWriter.println(message);
        printWriter.flush();
    }

    private int parseData(String cmdString, String readingString) {
        int result = 0;

        if (!readingString.matches(DATA_REGEX)) {
            LOGGER.log(Level.INFO, String.format("%s: nan", Util.getNameByCmdCode(cmdString)));
        } else {
            result = (int) Double.parseDouble(readingString);
            LOGGER.log(Level.INFO, String.format("%s: %d", Util.getNameByCmdCode(cmdString), result));
        }

        return result;
    }

    private boolean isDanger(String cmdString, int data) {
        if (data > Util.getThresByCmdCode(cmdString)) {
            return true;
        }

        return false;
    }

    private void startTimer(long startTime) {
        Thread timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.log(Level.INFO, "Timer started.");

                while (isTimerRunning) {
                    mainController.setClock(((System.currentTimeMillis() - startTime) / 1000.0));
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
                        mainController.setGaugeVelocity(velocity, dVelocity);
                        mainController.setGaugeAcceleration(acceleration, dAcceleration);
                        mainController.setGaugeRpmfl(rpm_fl, dRpm_fl);
                        mainController.setGaugeRpmfr(rpm_fr, dRpm_fr);
                        mainController.setGaugeRpmbl(rpm_bl, dRpm_bl);
                        mainController.setGaugeRpmbr(rpm_br, dRpm_br);
                        mainController.setGaugeVoltage(hp_volt, dHp_volt);
                        mainController.setGaugeTemp(hp_temp, dHp_temp);
                        mainController.setGaugeVoltage1(hp_volt1, dHp_volt1);
                        mainController.setGaugeTemp1(hp_temp1, dHp_temp1);
                        mainController.setGaugeLpbattery(lp_charge, dLp_charge);
                        mainController.setGaugeLpbattery1(lp_charge1, dLp_charge1);
                        mainController.setGaugeHpBattery(hp_charge, dHp_charge);
                        mainController.setGaugeHpBattery1(hp_charge1, dHp_charge1);
                        mainController.setImuIndicator(imu);
                        mainController.setProxi_FrontIndicator(proxi_front);
                        mainController.setProxi_RearIndicator(proxi_rear);
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
                mainController.enableBtnLaunch();
                mainController.enableBtnStop();
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
                mainController.setBrakeIndicatorOn();
                mainController.setStateLabel("EMERGENCY BRAKING");
                break;
            case 6:
                isTimerRunning = false;
                mainController.enableServicePropulsion();
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

        isCommunicating = true;
        mainController.setTelemetryIndicatorOn();
        updateGauges();

        while (scanner.hasNext()) {
            data = scanner.nextLine();
            cmdString = data.substring(0, 5);
            readingString = data.substring(5);

            switch (cmdString) {
                case "CMD01":
                    distance = parseData(cmdString, readingString);
                    dDistance = isDanger(cmdString, distance);
                    break;
                case "CMD02":
                    velocity = parseData(cmdString, readingString);
                    dVelocity = isDanger(cmdString, velocity);
                    break;
                case "CMD03":
                    acceleration = parseData(cmdString, readingString);
                    dAcceleration = isDanger(cmdString, acceleration);
                    break;
                case "CMD04":
                    rpm_fl = parseData(cmdString, readingString);
                    dRpm_fl = isDanger(cmdString, rpm_fl);
                    break;
                case "CMD05":
                    rpm_fr = parseData(cmdString, readingString);
                    dRpm_fr = isDanger(cmdString, rpm_fr);
                    break;
                case "CMD06":
                    rpm_bl = parseData(cmdString, readingString);
                    dRpm_bl = isDanger(cmdString, rpm_bl);
                    break;
                case "CMD07":
                    rpm_br = parseData(cmdString, readingString);
                    dRpm_br = isDanger(cmdString, rpm_br);
                    break;
                case "CMD08":
                    state = parseData(cmdString, readingString);
//                    dState = isDanger(cmdString, state);
                    setState(state);
                    break;
                case "CMD09":
                    hp_volt = parseData(cmdString, readingString);
                    dHp_volt = isDanger(cmdString, hp_volt);
                    break;
                case "CMD10":
                    hp_temp = parseData(cmdString, readingString);
                    dHp_temp = isDanger(cmdString, hp_temp);
                    break;
                case "CMD11":
                    hp_charge = parseData(cmdString, readingString);
                    dHp_charge = isDanger(cmdString, hp_charge);
                    break;
                case "CMD12":
                    hp_volt1 = parseData(cmdString, readingString);
                    dHp_volt1 = isDanger(cmdString, hp_volt1);
                    break;
                case "CMD13":
                    hp_temp1 = parseData(cmdString, readingString);
                    dHp_temp1 = isDanger(cmdString, hp_temp1);
                    break;
                case "CMD14":
                    hp_charge1 = parseData(cmdString, readingString);
                    dHp_charge1 = isDanger(cmdString, hp_charge1);
                    break;
                case "CMD15":
                    lp_charge = parseData(cmdString, readingString);
                    dLp_charge = isDanger(cmdString, lp_charge);
                    break;
                case "CMD16":
                    lp_charge1 = parseData(cmdString, readingString);
                    dLp_charge1 = isDanger(cmdString, lp_charge1);
                    break;
                case "CMD17":
                    imuReceived = parseData(cmdString, readingString);
                    if (imuReceived != 0) {
                        imu[0] = Integer.parseInt(Integer.toString(imuReceived).substring(0, 1));
                        imu[1] = Integer.parseInt(Integer.toString(imuReceived).substring(1, 2));
                        imu[2] = Integer.parseInt(Integer.toString(imuReceived).substring(2, 3));
                        imu[3] = Integer.parseInt(Integer.toString(imuReceived).substring(3));
                    }
                    break;
                case "CMD18":
                    proxi_frontReceived = parseData(cmdString, readingString);
                    if (proxi_frontReceived != 0) {
                        proxi_front[0] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(0, 1));
                        proxi_front[1] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(1, 2));
                        proxi_front[2] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(2, 3));
                        proxi_front[3] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(3, 4));
                        proxi_front[4] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(4, 5));
                        proxi_front[5] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(5, 6));
                        proxi_front[6] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(6, 7));
                        proxi_front[7] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(7));
                    }
                    break;
                case "CMD19":
                    proxi_rearReceived = parseData(cmdString, readingString);
                    if (proxi_rearReceived != 0) {
                        proxi_rear[0] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(0, 1));
                        proxi_rear[1] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(1, 2));
                        proxi_rear[2] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(2, 3));
                        proxi_rear[3] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(3, 4));
                        proxi_rear[4] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(4, 5));
                        proxi_rear[5] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(5, 6));
                        proxi_rear[6] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(6, 7));
                        proxi_rear[7] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(7));
                    }
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Should never reach here.");
                    break;
            }

            sendToSpaceX(status, team_id, acceleration, distance, velocity);
        }

        mainController.setTelemetryIndicatorOff();
        isCommunicating = false;
    }

    private void sendToSpaceX(byte status, byte team_id,
                              int acceleration, int position, int velocity) {
        try {
            spaceXBuffer.put(team_id);
            spaceXBuffer.put(status);
            spaceXBuffer.putInt(acceleration);
            spaceXBuffer.putInt(position);
            spaceXBuffer.putInt(velocity);
            spaceXBuffer.putInt(0);  // battery voltage
            spaceXBuffer.putInt(0);  // battery current
            spaceXBuffer.putInt(0);  // battery temperature
            spaceXBuffer.putInt(0);  // pod temperature
            spaceXBuffer.putInt(0);  // stripe count
            spaceXPacket = new DatagramPacket(spaceXBuffer.array(), spaceXBuffer.limit(), spaceXAddress, SPACE_X_PORT);
            spaceXSocket.send(spaceXPacket);
            spaceXBuffer.clear();
        } catch (IOException e) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Cannot send packets to SpaceX.", e);
            e.printStackTrace();
        }
    }

}
