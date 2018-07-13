package view.main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.*;

/**
 * Server class. Accepts TCP connection request from client (pod) and communicates data.
 *
 * @author Kofi and Isa, HYPED 17/18
 */
public class Server implements Runnable {

    private byte status = 1, team_id = 2;

    // Data parsing
    private static final String DATA_REGEX =  "^\\d*\\.?\\d+|\\d+\\.?\\d*$"; // "^[0-9]+$"
    private int distance, velocity, acceleration,
                rpm_fl, rpm_fr, rpm_br, rpm_bl,
                hp_volt, hp_temp, hp_charge, hp_volt1, hp_temp1, hp_charge1, lp_charge, lp_charge1,
                imuReceived, proxi_frontReceived, proxi_rearReceived, em_brakesReceived;
    private int state = 0;
    private int[] imu = new int[4];
    private int[] proxi_front = new int[8];
    private int[] proxi_rear = new int[8];
    private int[] em_brakes = new int[2];
    private double time = 0;
    // Danger flags, true if value exceeds threshold
    private boolean dDistance, dVelocity, dAcceleration,
                    dRpm_fl, dRpm_fr, dRpm_br, dRpm_bl,
                    dHp_volt, dHp_temp, dHp_charge, dHp_volt1, dHp_temp1, dHp_charge1, dLp_charge, dLp_charge1;
    private String data, cmdString, readingString;

    // TCP/IP connection to pod
    private static final int PORT = 5695;
    private static final int ACK_FROM_SERVER = 0;
    private ServerSocket serverSocket;
    private Socket podSocket;
    private PrintWriter printWriter;
    private Scanner scanner;
    // UDP connection to SpaceX
    private static final String SPACE_X_IP = "localhost";  // TODO: SpaceX's IP is 192.168.0.1
    private static final int SPACE_X_PORT = 3000;  // SpaceX's UDP port is 3000
    private InetAddress spaceXAddress;
    private DatagramSocket spaceXSocket;
    private DatagramPacket spaceXPacket;
    private ByteBuffer spaceXBuffer = ByteBuffer.allocate(34); // BigEndian by default

    // GUI related stuff
    private MainController mainController;
    private boolean isPodRunning = false;
    private boolean isCommunicating = false;

    // For logging
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
            this.mainController = controller;
            spaceXAddress = InetAddress.getByName(SPACE_X_IP);
            spaceXSocket = new DatagramSocket();
            serverSocket = new ServerSocket(PORT);
            LOGGER.log(Level.INFO, "Server initialised; Controller assigned.");
        } catch (UnknownHostException e) {
            LOGGER.log(Level.SEVERE, "Connection to SpaceX failed.");
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
        mainController.setUpdatesLabel("Awaiting connection from pod...");
        LOGGER.log(Level.INFO, "Awaiting connection from pod...");

        try {
            podSocket = serverSocket.accept();
            scanner = new Scanner(podSocket.getInputStream());
            printWriter = new PrintWriter(podSocket.getOutputStream());
            sendToPod(ACK_FROM_SERVER);
            mainController.setUpdatesLabel("Client connected.");
            LOGGER.log(Level.INFO, "Accepted connection from client.");
            startCommunication();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "run() failed.");
            e.printStackTrace();
        } finally {
            LOGGER.log(Level.INFO, "Communication terminated. Closing socket, scanner and printwriter.");
            isPodRunning = false;
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

            return;
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
        if ((Util.isLowerThresKey(cmdString) && (data < Util.getLowerThresByCmdCode(cmdString))) ||
                data > Util.getUpperThresByCmdCode(cmdString)) {
            return true;
        }

        return false;
    }

    private void startTimer(long startTime) {
        Thread timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.log(Level.INFO, "Timer started.");

                while (isPodRunning) {
                    time = (((System.currentTimeMillis() - startTime) / 1000.0)*10)/10.00;
                    mainController.setClock(time);
                    System.out.println(time);
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
                        mainController.setGaugeHpBattery(hp_charge, dHp_charge);
                        mainController.setGaugeVoltage1(hp_volt1, dHp_volt1);
                        mainController.setGaugeTemp1(hp_temp1, dHp_temp1);
                        mainController.setGaugeHpBattery1(hp_charge1, dHp_charge1);
                        mainController.setGaugeLpbattery(lp_charge, dLp_charge);
                        mainController.setGaugeLpbattery1(lp_charge1, dLp_charge1);
                        mainController.setImuIndicator(imu);
                        mainController.setProxi_FrontIndicator(proxi_front);
                        mainController.setProxi_RearIndicator(proxi_rear);
                        mainController.setBrakeIndicator(em_brakes);
                    }
                }));

                gaugeLag.setCycleCount(Timeline.INDEFINITE);
                gaugeLag.play();
            }
        });

        gaugeThread.start();
    }

    public void resetAll() {
        distance = 0;
        velocity = 0; dVelocity = false;
        acceleration = 0; dAcceleration = false;
        rpm_fl = 0; dRpm_fl = false;        // RPM
        rpm_fr = 0; dRpm_fr = false;
        rpm_bl = 0; dRpm_bl = false;
        rpm_br = 0; dRpm_br = false;
        hp_volt = 0; dHp_volt = false;      // HP Batteries
        hp_temp = 0; dHp_temp = false;
        hp_charge = 0; dHp_charge = false;
        hp_volt1 = 0; dHp_volt1 = false;
        hp_temp1 = 0; dHp_temp1 = false;
        hp_charge1 = 0; dHp_charge1 = false;
        lp_charge = 0; dLp_charge = false;  // LP batteries
        lp_charge1 = 0; dLp_charge1 = false;
        Arrays.fill(imu, 0);           // Sensors stuff
        Arrays.fill(proxi_front, 0);
        Arrays.fill(proxi_rear, 0);
        Arrays.fill(em_brakes, 0);
    }

    private void setState(int state) {
        switch(state) {
            case 0:
                mainController.setStateLabel("IDLE");
                status = 1;
                break;
            case 1:
                mainController.setStateLabel("CALIBRATING");
                status = 1;
                break;
            case 2:
                mainController.enableBtnLaunch();
                mainController.enableBtnStop();
                mainController.setStateLabel("READY");
                status = 2;
                break;
            case 3:
                if (!isPodRunning) {
                    startTimer(System.currentTimeMillis());
                    isPodRunning = true;
                }
                mainController.setStateLabel("ACCELERATING");
                status = 3;
                break;
            case 4:
                mainController.setStateLabel("DECELERATING");
                status = 3;
                break;
            case 5:
                mainController.setStateLabel("EMERGENCY");
                status = 0;
                break;
            case 6:
                isPodRunning = false;
                mainController.enableServicePropulsion();
                mainController.setStateLabel("RUN COMPLETE");
                status = 1;
                break;
            case 7:
                isPodRunning = false;
                mainController.setStateLabel("FAILURE STOPPED");
                status = 0;
                break;
            case 8:
                mainController.setStateLabel("EXITING");
                status = 1;
                break;
            case 9:
                mainController.setStateLabel("FINISHED");
                status = 1;
                break;
            case 10:
                mainController.setStateLabel("INVALID");
                status = 0;
                break;
            default:
                status = 1; // TODO: Double check this
                LOGGER.log(Level.WARNING, "Should never reach here.");
                break;
        }
    }

    private void startCommunication() {
        if (podSocket == null) {
            LOGGER.log(Level.SEVERE, "NO POD SOCKET. Should never reach here.");
        }

        isCommunicating = true;
        mainController.setTelemetryIndicatorOn();
        mainController.setUpdatesLabel("Telemetry operational");
        LOGGER.log(Level.INFO, "Communication between pod and base-station started.");

        // actually reads data
        updateGauges();
        readDataFromSocket();

        isCommunicating = false;
        mainController.setTelemetryIndicatorOff();
        mainController.setUpdatesLabel("Connection lost");
        LOGGER.log(Level.INFO, "Communication finished.");
    }

    private void readDataFromSocket() {
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
                case "CMD20":
                    em_brakesReceived = parseData(cmdString, readingString);
                    if (em_brakesReceived != 0) {
                        em_brakes[0] = Integer.parseInt(Integer.toString(em_brakesReceived).substring(0, 1));
                        em_brakes[1] = Integer.parseInt(Integer.toString(em_brakesReceived).substring(1));
                    }
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Should never reach here.");
                    break;
            }

            sendToSpaceX(status, team_id, acceleration, distance, velocity);
        }
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
