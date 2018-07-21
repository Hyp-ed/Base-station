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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

/**
 * Server class. Accepts TCP connection request from client (pod) and communicates data.
 *
 * @author Kofi and Isa, HYPED 17/18
 */
public class Server implements Runnable {

    private byte status = 1, team_id = 2;

    // Data parsing
    private static final String DATA_REGEX = "^-?\\d*\\.?\\d+|\\d+\\.?\\d*$";  // "^[0-9]+$"
    private int batModStatus, navModStatus, senModStatus, mtrModStatus,
            distance, velocity, acceleration,
            rpm_fl, rpm_fr, rpm_br, rpm_bl,
            hp_volt, hp_temp, hp_charge, hp_volt1, hp_temp1, hp_charge1, lp_charge, lp_charge1,
            imuReceived, em_brakesReceived, regen, regen1,
            hp_current, hp_current1, lowest_cell, highest_cell, lowest_cell1, highest_cell1,
            lp_voltage, lp_voltage1, lp_current, lp_current1;
            //    proxi_frontReceived, proxi_rearReceived,
    private int state = 0;
    private int min_charge = 100;
    private int min_charge1 = 100;
    private int[] imu = new int[4];
//    private int[] proxi_front = new int[8];
//    private int[] proxi_rear = new int[8];
    private int[] em_brakes = new int[2];
    // Danger flags, true if value exceeds threshold
    private boolean dVelocity, dAcceleration,
            dRpm_fl, dRpm_fr, dRpm_br, dRpm_bl,
            dHp_volt, dHp_temp, dHp_charge, dHp_volt1, dHp_temp1, dHp_charge1, dLp_charge, dLp_charge1,
            dHp_current, dHp_current1, dLowest_cell, dHighest_cell, dLowest_cell1, dHighest_cell1,
            dLp_voltage, dLp_voltage1, dLp_current, dLp_current1;
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
    private ByteBuffer spaceXBuffer = ByteBuffer.allocate(34);  // BigEndian by default

    // GUI related stuff
    private MainController mainController;
    private boolean isPodRunning = false;
    private boolean isCommunicating = false;

    // For logging
    private static Logger LOGGER;
    private static Handler loggerHandler = null;

// -------------------------------------------------------------------------------------------------
// Set Logger
// -------------------------------------------------------------------------------------------------

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        LOGGER = Logger.getLogger(Server.class.getName());

        try {
            loggerHandler = new FileHandler("logger.log");
            SimpleFormatter simple = new SimpleFormatter();
            loggerHandler.setFormatter(simple);
            LOGGER.addHandler(loggerHandler);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to set up logger.");
            e.printStackTrace();
        }
    }

// -------------------------------------------------------------------------------------------------
// Server class
// -------------------------------------------------------------------------------------------------

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
        // Run the thread which constantly update gauges
        updateGauges();
        // Run the thread which constantly sends to SpaceX
        Runnable udpRunnable = new Runnable() {
            public void run() {
                if (isCommunicating) sendToSpaceX();
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(udpRunnable, 0, 200, TimeUnit.MILLISECONDS);


        while (true) {
            try {
                podSocket = serverSocket.accept();
                scanner = new Scanner(podSocket.getInputStream());
                printWriter = new PrintWriter(podSocket.getOutputStream());
                sendToPod(ACK_FROM_SERVER);
                resetAll();
                mainController.setUpdatesLabel("Client connected.");
                LOGGER.log(Level.INFO, "Accepted connection from client.");
                startCommunication();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "run() failed.");
                e.printStackTrace();
            } finally {
                LOGGER.log(Level.INFO, "Communication terminated. Closing socket, scanner and printwriter.");
                try {
                    podSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                scanner.close();
                printWriter.close();
            }
        }
    }

    public boolean isCommunicating() {
        return isCommunicating;
    }

// -------------------------------------------------------------------------------------------------
// Send data, Parse incoming data
// -------------------------------------------------------------------------------------------------

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

// -------------------------------------------------------------------------------------------------
// Timer
// -------------------------------------------------------------------------------------------------

    private void startTimer(long startTime) {
        Thread timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.log(Level.INFO, "Timer started.");

                while (isPodRunning) {
                    mainController.setClock(Math.round((System.currentTimeMillis() - startTime) / 100.0) / 10.00);
                }
            }
        });

        timerThread.start();
    }


// -------------------------------------------------------------------------------------------------
// Manipulate GUI
// -------------------------------------------------------------------------------------------------

    private void updateGauges() {
        Thread gaugeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Timeline gaugeLag = new Timeline(new KeyFrame(Duration.seconds(0.3), new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        mainController.setBatIndicator(batModStatus);
                        mainController.setNavIndicator(navModStatus);
                        mainController.setSenIndicator(senModStatus);
                        mainController.setMtrIndicator(mtrModStatus);
                        mainController.setDistanceMeter(distance);
                        mainController.setGaugeVelocity(velocity, dVelocity);
                        mainController.setGaugeAcceleration(acceleration, dAcceleration);
                        mainController.setGaugeRpmfl(rpm_fl, dRpm_fl);
                        mainController.setGaugeRpmfr(rpm_fr, dRpm_fr);
                        mainController.setGaugeRpmbl(rpm_bl, dRpm_bl);
                        mainController.setGaugeRpmbr(rpm_br, dRpm_br);
                        mainController.setImuIndicator(imu);
//                        mainController.setProxi_FrontIndicator(proxi_front);
//                        mainController.setProxi_RearIndicator(proxi_rear);
                        mainController.setBrakeIndicator(em_brakes);
                        mainController.setGaugeVoltage(hp_volt, dHp_volt);
                        mainController.setGaugeHpCurrent(hp_current, dHp_current);
                        mainController.setGaugeHpBattery(hp_charge, dHp_charge);
                        mainController.setGaugeTemp(hp_temp, dHp_temp);
                        mainController.setLowCell(lowest_cell);
                        mainController.setHighCell(highest_cell);
                        mainController.setGaugeVoltage1(hp_volt1, dHp_volt1);
                        mainController.setGaugeHpCurrent1(hp_current1, dHp_current1);
                        mainController.setGaugeHpBattery1(hp_charge1, dHp_charge1);
                        mainController.setGaugeTemp1(hp_temp1, dHp_temp1);
                        mainController.setLowCell1(lowest_cell1);
                        mainController.setHighCell1(highest_cell1);
                        mainController.setGaugeLpVoltage(lp_voltage, dLp_voltage);
                        mainController.setGaugeLpCurrent(lp_current, dLp_current);
                        mainController.setGaugeLpBattery(lp_charge, dLp_charge);
                        mainController.setGaugeLpVoltage1(lp_voltage1, dLp_voltage1);
                        mainController.setGaugeLpCurrent1(lp_current1, dLp_current1);
                        mainController.setGaugeLpBattery1(lp_charge1, dLp_charge1);
                    }
                }));

                gaugeLag.setCycleCount(Timeline.INDEFINITE);
                gaugeLag.play();
            }
        });

        gaugeThread.start();
    }

    public void resetAll() {
        status = 1;
        batModStatus = 0; navModStatus = 0; senModStatus = 0; mtrModStatus = 0;
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
        hp_current = 0; dHp_current = false;
        lowest_cell = 0; dLowest_cell = false;
        highest_cell = 0; dHighest_cell = false;
        hp_volt1 = 0; dHp_volt1 = false;
        hp_temp1 = 0; dHp_temp1 = false;
        hp_charge1 = 0; dHp_charge1 = false;
        hp_current1 = 0; dHp_current1 = false;
        lowest_cell1 = 0; dLowest_cell1 = false;
        highest_cell1 = 0; dHighest_cell1 = false;
        lp_voltage = 0; dLp_voltage = false;
        lp_current = 0; dLp_current = false;
        lp_charge = 0; dLp_charge = false;  // LP batteries
        lp_voltage1 = 0; dLp_voltage1 = false;
        lp_current1 = 0; dLp_current1 = false;
        lp_charge1 = 0; dLp_charge1 = false;
        Arrays.fill(imu, 0);           // Sensors stuff
//        Arrays.fill(proxi_front, 0);
//        Arrays.fill(proxi_rear, 0);
        Arrays.fill(em_brakes, 0);

        // GUI stuff
        mainController.setClock(0);
        mainController.setTelemetryIndicatorOff();
        mainController.setStateLabel("IDLE");
        mainController.disableBtnLaunch();
        mainController.disableServicePropulsion();
        mainController.trackLengthWarningOff();
    }

    public String regenBraking() {
        regen = hp_charge - min_charge;
        regen1 = hp_charge1 - min_charge1;
        String braking = "HP = " + regen + "% HP1 = " + regen1 + "%";
        return braking;
    }


// -------------------------------------------------------------------------------------------------
// State Machine
// -------------------------------------------------------------------------------------------------

    private void setState(int state) {
        switch (state) {
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
                mainController.setStateLabel("READY");
                status = 2;
                break;
            case 3:
                if (!isPodRunning) {
                    startTimer(System.currentTimeMillis());
                    isPodRunning = true;
                }
                mainController.setStateLabel("ACCELERATING");
                mainController.disableBtnLaunch();
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
                mainController.setUpdatesLabel("Regenerative Braking: " + regenBraking());
                LOGGER.log(Level.INFO, "Regenerative Braking: " + regenBraking());
                status = 1;
                break;
            case 7:
                isPodRunning = false;
                mainController.setStateLabel("FAILURE STOP");
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
                status = 0; // TODO: Double check this
                LOGGER.log(Level.WARNING, "Should never reach here.");
                break;
        }
    }


// -------------------------------------------------------------------------------------------------
// Start communication. Read incoming data.
// -------------------------------------------------------------------------------------------------

    private void startCommunication() {
        if (podSocket == null) {
            LOGGER.log(Level.SEVERE, "NO POD SOCKET. Should never reach here.");
        }

        isCommunicating = true;
        mainController.setTelemetryIndicatorOn();
        mainController.setUpdatesLabel("Telemetry operational");
        LOGGER.log(Level.INFO, "Communication between pod and base-station started.");

        // actually reads data
        readDataFromSocket();


        isCommunicating = false;
        isPodRunning = false;
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
                    state = parseData(cmdString, readingString);
                    setState(state);
                    break;
                case "CMD02":
                    batModStatus = parseData(cmdString, readingString);
                    break;
                case "CMD03":
                    navModStatus = parseData(cmdString, readingString);
                    break;
                case "CMD04":
                    senModStatus = parseData(cmdString, readingString);
                    break;
                case "CMD05":
                    mtrModStatus = parseData(cmdString, readingString);
                    break;
                case "CMD06":
                    distance = parseData(cmdString, readingString);
                    if (distance < 0) distance = 0;
                    break;
                case "CMD07":
                    velocity = parseData(cmdString, readingString);
                    if (velocity < 0) velocity = 0;
                    dVelocity = isDanger(cmdString, velocity);
                    break;
                case "CMD08":
                    acceleration = parseData(cmdString, readingString);
                    dAcceleration = isDanger(cmdString, acceleration);
                    break;
                case "CMD09":
                    hp_volt = parseData(cmdString, readingString);
                    dHp_volt = isDanger(cmdString, hp_volt);
                    break;
                case "CMD10":
                    hp_current = parseData(cmdString, readingString);
                    dHp_current = isDanger(cmdString, hp_current);
                    break;
                case "CMD11":
                    hp_charge = parseData(cmdString, readingString);
                    dHp_charge = isDanger(cmdString, hp_charge);
                    break;
                case "CMD12":
                    hp_temp = parseData(cmdString, readingString);
                    dHp_temp = isDanger(cmdString, hp_temp);
                    break;
                case "CMD13":
                    lowest_cell = parseData(cmdString, readingString);
                    dLowest_cell = isDanger(cmdString, lowest_cell);
                    break;
                case "CMD14":
                    highest_cell = parseData(cmdString, readingString);
                    dHighest_cell = isDanger(cmdString, highest_cell);
                    break;
                case "CMD15":
                    hp_volt1 = parseData(cmdString, readingString);
                    dHp_volt1 = isDanger(cmdString, hp_volt1);
                    break;
                case "CMD16":
                    hp_current1 = parseData(cmdString, readingString);
                    dHp_current1 = isDanger(cmdString, hp_current1);
                    break;
                case "CMD17":
                    hp_charge1 = parseData(cmdString, readingString);
                    dHp_charge1 = isDanger(cmdString, hp_charge1);
                    break;
                case "CMD18":
                    hp_temp1 = parseData(cmdString, readingString);
                    dHp_temp1 = isDanger(cmdString, hp_temp1);
                    break;
                case "CMD19":
                    lowest_cell1 = parseData(cmdString, readingString);
                    dLowest_cell1 = isDanger(cmdString, lowest_cell1);
                    break;
                case "CMD20":
                    highest_cell1 = parseData(cmdString, readingString);
                    dHighest_cell1 = isDanger(cmdString, highest_cell1);
                    break;
                case "CMD21":
                    lp_voltage = parseData(cmdString, readingString);
                    dLp_voltage = isDanger(cmdString, lp_voltage);
                    break;
                case "CMD22":
                    lp_current = parseData(cmdString, readingString);
                    dLp_current = isDanger(cmdString, lp_current);
                    break;
                case "CMD23":
                    lp_charge = parseData(cmdString, readingString);
                    dLp_charge = isDanger(cmdString, lp_charge);
                    break;
                case "CMD24":
                    lp_voltage1 = parseData(cmdString, readingString);
                    dLp_voltage1 = isDanger(cmdString, lp_voltage1);
                    break;
                case "CMD25":
                    lp_current1 = parseData(cmdString, readingString);
                    dLp_current1 = isDanger(cmdString, lp_current1);
                    break;
                case "CMD26":
                    lp_charge1 = parseData(cmdString, readingString);
                    dLp_charge1 = isDanger(cmdString, lp_charge1);
                    break;
                case "CMD27":
                    rpm_fl = parseData(cmdString, readingString);
                    dRpm_fl = isDanger(cmdString, rpm_fl);
                    break;
                case "CMD28":
                    rpm_fr = parseData(cmdString, readingString);
                    dRpm_fr = isDanger(cmdString, rpm_fr);
                    break;
                case "CMD29":
                    rpm_bl = parseData(cmdString, readingString);
                    dRpm_bl = isDanger(cmdString, rpm_bl);
                    break;
                case "CMD30":
                    rpm_br = parseData(cmdString, readingString);
                    dRpm_br = isDanger(cmdString, rpm_br);
                    break;
                case "CMD31":
                    imuReceived = parseData(cmdString, readingString);
                    System.out.println("imu received " + imuReceived);
                    if (imuReceived != 0) {
                        imu[0] = Integer.parseInt(Integer.toString(imuReceived).substring(0, 1));
                        imu[1] = Integer.parseInt(Integer.toString(imuReceived).substring(1, 2));
                        imu[2] = Integer.parseInt(Integer.toString(imuReceived).substring(2, 3));
                        imu[3] = Integer.parseInt(Integer.toString(imuReceived).substring(3));
                    }
                    break;
                case "CMD32":
                    em_brakesReceived = parseData(cmdString, readingString);
                    if (em_brakesReceived != 0) {
                        em_brakes[0] = Integer.parseInt(Integer.toString(em_brakesReceived).substring(0, 1));
                        em_brakes[1] = Integer.parseInt(Integer.toString(em_brakesReceived).substring(1));
                    }
                    break;
//                case "CMD33":
//                    proxi_frontReceived = parseData(cmdString, readingString);
//                    if (proxi_frontReceived != 0) {
//                        proxi_front[0] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(0, 1));
//                        proxi_front[1] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(1, 2));
//                        proxi_front[2] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(2, 3));
//                        proxi_front[3] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(3, 4));
//                        proxi_front[4] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(4, 5));
//                        proxi_front[5] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(5, 6));
//                        proxi_front[6] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(6, 7));
//                        proxi_front[7] = Integer.parseInt(Integer.toString(proxi_frontReceived).substring(7));
//                    }
//                    break;
//                case "CMD34":
//                    proxi_rearReceived = parseData(cmdString, readingString);
//                    if (proxi_rearReceived != 0) {
//                        proxi_rear[0] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(0, 1));
//                        proxi_rear[1] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(1, 2));
//                        proxi_rear[2] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(2, 3));
//                        proxi_rear[3] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(3, 4));
//                        proxi_rear[4] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(4, 5));
//                        proxi_rear[5] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(5, 6));
//                        proxi_rear[6] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(6, 7));
//                        proxi_rear[7] = Integer.parseInt(Integer.toString(proxi_rearReceived).substring(7));
//                    }
//                    break;
                default:
                    LOGGER.log(Level.WARNING, "Should never reach here. Data: " + data);
                    break;
            }
        }
    }


// -------------------------------------------------------------------------------------------------
// UDP
// -------------------------------------------------------------------------------------------------

    private void sendToSpaceX() {
        try {
            spaceXBuffer.put(team_id);
            spaceXBuffer.put(status);
            spaceXBuffer.putInt(acceleration * 100);  // acceleration (cm/s2)
            spaceXBuffer.putInt(distance * 100);      // position (cm)
            spaceXBuffer.putInt(velocity * 100);      // velocity (cm/s)
            spaceXBuffer.putInt(0);                   // battery voltage
            spaceXBuffer.putInt(0);                   // battery current
            spaceXBuffer.putInt(0);                   // battery temperature
            spaceXBuffer.putInt(0);                   // pod temperature
            spaceXBuffer.putInt(0);                   // stripe count
            spaceXPacket = new DatagramPacket(spaceXBuffer.array(), spaceXBuffer.limit(), spaceXAddress, SPACE_X_PORT);
            spaceXSocket.send(spaceXPacket);
            spaceXBuffer.clear();
        } catch (IOException e) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Cannot send packets to SpaceX.", e);
            e.printStackTrace();
        }
    }

}
