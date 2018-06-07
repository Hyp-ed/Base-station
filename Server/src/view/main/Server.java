package view.main;

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
    public static final int ACK_FROM_SERVER = 4;
    private static final String DATA_REGEX = "^[0-9]+$";

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
            System.out.println("Hello");
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

    private void startCommunication() {
        LOGGER.log(Level.INFO, "Communication between pod and base-station started.");

        if (podSocket == null) {
            LOGGER.log(Level.SEVERE, "NO POD SOCKET. Should never reach here.");
        }

        int distance, velocity, acceleration, stripe_count,
            rpm_fl, rpm_fr, rpm_br, rpm_bl,
            hp_volt, hp_temp, hp_volt1, hp_temp1;
        int state = 0;
        String data, cmdString, readingString;

        mainController.setTelemetryIndicator();

        while (scanner.hasNext()) {
            data = scanner.nextLine();
            cmdString = data.substring(0, 5);
            readingString = data.substring(5);

            switch (data.substring(0, 5)) {
                case "CMD01":
                    distance = parseData(cmdString, readingString);
                    mainController.setDistanceMeter(distance);
                    break;
                case "CMD02":
                    velocity = parseData(cmdString, readingString);

                    if (!isTimerRunning && velocity == 0) {
                        startTimer(System.currentTimeMillis());
                        isTimerRunning = true;
                    } else if (velocity >= 100) {
                        isTimerRunning = false;
                    }

                    mainController.setGaugeVelocity(velocity);
                    break;
                case "CMD03":
                    acceleration = parseData(cmdString, readingString);
                    mainController.setGaugeAcceleration(acceleration);
                    break;
                case "CMD04":
                    break;
                case "CMD05":
                    rpm_fl = parseData(cmdString, readingString);
                    mainController.setGaugeRpmfl(rpm_fl);
                    break;
                case "CMD06":
                    rpm_fr = parseData(cmdString, readingString);
                    mainController.setGaugeRpmfr(rpm_fr);
                    break;
                case "CMD07":
                    rpm_bl = parseData(cmdString, readingString);
                    mainController.setGaugeRpmbl(rpm_bl);
                    break;
                case "CMD08":
                    rpm_br = parseData(cmdString, readingString);
                    mainController.setGaugeRpmbr(rpm_br);
                    break;
                case "CMD09":
                    state = parseData(cmdString, readingString);

                    if(state == 3){
                        mainController.setBrakeIndicator();
                    }
//                    mainController.setGaugeState(state);  // TODO(Kofi): implement state gadget
                    break;
                case "CMD10":
                    hp_volt = parseData(cmdString, readingString);
                    mainController.setGaugeVoltage(hp_volt);
                    break;
                case "CMD11":
                    hp_temp = parseData(cmdString, readingString);
                    mainController.setGaugeTemp(hp_temp);
                    break;
                case "CMD12":
                    hp_volt1 = parseData(cmdString, readingString);
                    mainController.setGaugeVoltage1(hp_volt1);
                    break;
                case "CMD13":
                    hp_temp1 = parseData(cmdString, readingString);
                    mainController.setGaugeTemp1(hp_temp1);
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
