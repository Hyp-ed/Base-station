package view.main;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {

//    private final static Logger LOGGER = Logger.getLogger(Server.class.getName());
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

    public Server(MainController controller) {
        try {
            serverSocket = new ServerSocket(PORT);
            this.mainController = controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Awaiting connection from pod...");

        try {
            podSocket = serverSocket.accept();
            scanner = new Scanner(podSocket.getInputStream());
            printWriter = new PrintWriter(podSocket.getOutputStream());
            loggerStream = new PrintStream("logger.txt");
            System.setOut(loggerStream);
            sendToPod(ACK_FROM_SERVER);
            startCommunication();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Closes scanner, printwriter and printstreamer.");
            isTimerRunning = false;
            scanner.close();
            printWriter.close();
            loggerStream.close();
        }
    }

    private void startTimer(long startTime) {
        Thread timerThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // TODO(Isa): implement escape condition
                while (isTimerRunning) {
                    mainController.setClock((int) ((System.currentTimeMillis() - startTime) / 1000.0));
                }
            }
        });

        timerThread.start();
    }

    private void startCommunication() {
        if (podSocket == null) {
            System.err.println("ERROR: NO POD SOCKET");
            // TODO: exit system
        }

        int distance, velocity, acceleration, stripe_count,
            rpm_fl, rpm_fr, rpm_br, rpm_bl, state,
            hp_volt, hp_temp, hp_volt1, hp_temp1;
        String data;

        while (scanner.hasNext()) {
            data = scanner.nextLine();

            switch (data.substring(0, 5)) {
                case "CMD01":
                    if (!data.substring(5).matches(DATA_REGEX)) {
                        distance = 0;
                        System.out.println("distance: nan");
                    } else {
                        distance = (int) Double.parseDouble(data.substring(5));
                        System.out.println("distance: " + distance);
                    }

                    mainController.setDistanceMeter(distance);
                    break;
                case "CMD02":
                    if (!data.substring(5).matches(DATA_REGEX)) {
                        velocity = 0;
                        System.out.println("velocity: nan");
                    } else {
                        velocity = (int) Double.parseDouble(data.substring(5));
                        System.out.println("velocity: " + velocity);
                    }

                    if (!isTimerRunning && velocity == 0) {
                        startTimer(System.currentTimeMillis());
                        isTimerRunning = true;
                    } else if (velocity >= 100) {
                        isTimerRunning = false;
                    }

                    mainController.setGaugeVelocity(velocity);
                    break;
                case "CMD03":
                    if (!data.substring(5).matches(DATA_REGEX)) {
                        acceleration = 0;
                        System.out.println("acceleration: nan");
                    } else {
                        acceleration = (int) Double.parseDouble(data.substring(5));
                        System.out.println("acceleration: " + acceleration);
                    }

                    mainController.setGaugeAcceleration(acceleration);
                    break;
                case "CMD04":
                    if (!data.substring(5).matches(DATA_REGEX)) {
                        stripe_count = 0;
                        System.out.println("stripe count: nan");
                    } else {
                        stripe_count = (int) Double.parseDouble(data.substring(5));
                        System.out.println("stripe count: " + stripe_count);
                    }

                    break;
                case "CMD05":
                    if (!data.substring(5).matches(DATA_REGEX)) {
                        rpm_fl = 0;
                        System.out.println("rpm fl: nan");
                    } else {
                        rpm_fl = (int) Double.parseDouble(data.substring(5));
                        System.out.println("rpm fl: " + rpm_fl);
                    }

                    mainController.setGaugeRpmfl(rpm_fl);
                    break;
                case "CMD06":
                    if (!data.substring(5).matches(DATA_REGEX)) {
                        rpm_fr = 0;
                        System.out.println("rpm fr: nan");
                    } else {
                        rpm_fr = (int) Double.parseDouble(data.substring(5));
                        System.out.println("rpm fr: " + rpm_fr);
                    }

                    mainController.setGaugeRpmfr(rpm_fr);
                    break;
                case "CMD07":
                    if (!data.substring(5).matches(DATA_REGEX)) {
                        rpm_bl = 0;
                        System.out.println("rpm bl: nan");
                    } else {
                        rpm_bl = (int) Double.parseDouble(data.substring(5));
                        System.out.println("rpm bl: " + rpm_bl);
                    }

                    mainController.setGaugeRpmbl(rpm_bl);
                    break;
                case "CMD08":
                    if (!data.substring(5).matches(DATA_REGEX)) {
                        rpm_br = 0;
                        System.out.println("rpm br: nan");
                    } else {
                        rpm_br = (int) Double.parseDouble(data.substring(5));
                        System.out.println("rpm br: " + rpm_br);
                    }

                    mainController.setGaugeRpmbr(rpm_br);
                    break;
                case "CMD09":
                    if (!data.substring(5).matches(DATA_REGEX)) {
                        System.out.println("Should never reach here");
                    } else {
                        state = (int) Double.parseDouble(data.substring(5));
                        System.out.println("state: " + state);
                    }

//                    mainController.setGaugeState(state);  // TODO(Kofi): implement state gadget
                    break;
                case "CMD10":
                    if (!data.substring(5).matches(DATA_REGEX)) {
                        hp_volt = 0;
                        System.out.println("hp volt: nan");
                    } else {
                        hp_volt = (int) Double.parseDouble(data.substring(5));
                        System.out.println("hp volt: " + hp_volt);
                    }

                    mainController.setGaugeVoltage(hp_volt);
                    break;
                case "CMD11":
                    if (!data.substring(5).matches(DATA_REGEX)) {
                        hp_temp = 0;
                        System.out.println("hp temp: nan");
                    } else {
                        hp_temp = (int) Double.parseDouble(data.substring(5));
                        System.out.println("hp temp: " + hp_temp);
                    }

                    mainController.setGaugeTemp(hp_temp);
                    break;
                case "CMD12":
                    if (!data.substring(5).matches(DATA_REGEX)) {
                        hp_volt1 = 0;
                        System.out.println("hp volt1: nan");
                    } else {
                        hp_volt1 = (int) Double.parseDouble(data.substring(5));
                        System.out.println("hp volt1: " + hp_volt1);
                    }

                    mainController.setGaugeVoltage1(hp_volt1);
                    break;
                case "CMD13":
                    if (!data.substring(5).matches(DATA_REGEX)) {
                        hp_temp1 = 0;
                        System.out.println("hp temp1: nan");
                    } else {
                        hp_temp1 = (int) Double.parseDouble(data.substring(5));
                        System.out.print("hp temp1: " + hp_temp1);
                    }

                    mainController.setGaugeTemp1(hp_temp1);
                    break;
                default:
                    System.out.println("Should never reach here.");

                    break;
            }

            //sendToSpaceX(status, team_id, acceleration, distance, velocity);
        }
    }

    public void sendToPod(int message) {
        if (podSocket == null) {
            System.err.println("ERROR: NO POD SOCKET");
            // TODO: exit system
        }

        System.out.println(String.format("Sending %s to pod", message));
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
