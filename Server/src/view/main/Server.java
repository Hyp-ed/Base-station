package view.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {

    private static final int PORT = 5695;
    private static final int SPACE_X_PORT = 4445;
    public static final int ACK_FROM_SERVER = 4;

    private ServerSocket serverSocket;
    private Socket podSocket;
    private PrintWriter printWriter;
    private Scanner scanner;
    private MainController mainController;

    byte status = 1, team_id = 2;
    int distance, velocity, acceleration, stripe_count,
            rpm_fl, rpm_fr, rpm_br, rpm_bl, state;
    String data;
    boolean isTimerRunning = false;

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
            sendToPod(ACK_FROM_SERVER);
            startCommunication();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Closes scanner and printwriter");
            scanner.close();
            printWriter.close();
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

        while (scanner.hasNext()) {
            data = scanner.nextLine();

            switch(data.substring(0, 5)) {
                case "CMD01":
                    if (!data.substring(5).matches("^[0-9]+$")) {
                        distance = 0;
                        System.out.println("distance: nan");
                    } else {
                        distance = (int) Double.parseDouble(data.substring(5));
                        System.out.println("distance: " + distance);
                    }

                    mainController.setDistanceMeter(distance);
                    break;
                case "CMD02":
                    if (!data.substring(5).matches("^[0-9]+$")) {
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
                    if (!data.substring(5).matches("^[0-9]+$")) {
                        acceleration = 0;
                        System.out.println("acceleration: nan");
                    } else {
                        acceleration = (int) Double.parseDouble(data.substring(5));
                        System.out.println("acceleration: " + acceleration);
                    }

                    mainController.setGaugeAcceleration(acceleration);
                    break;
                case "CMD04":
                    if (!data.substring(5).matches("^[0-9]+$")) {
                        stripe_count = 0;
                        System.out.println("stripe count: nan");
                    } else {
                        stripe_count = (int) Double.parseDouble(data.substring(5));
                        System.out.println("stripe count: " + stripe_count);
                    }

                    break;
                case "CMD05":
                    if (!data.substring(5).matches("^[0-9]+$")) {
                        rpm_fl = 0;
                        System.out.println("rpm fl: nan");
                    } else {
                        rpm_fl = (int) Double.parseDouble(data.substring(5));
                        System.out.println("rpm fl: " + rpm_fl);
                    }

                    mainController.setGaugeRpmfl(rpm_fl);
                    break;
                case "CMD06":
                    if (!data.substring(5).matches("^[0-9]+$")) {
                        rpm_fr = 0;
                        System.out.println("rpm fr: nan");
                    } else {
                        rpm_fr = (int) Double.parseDouble(data.substring(5));
                        System.out.println("rpm fr: " + rpm_fr);
                    }

                    mainController.setGaugeRpmfr(rpm_fr);
                    break;
                case "CMD07":
                    if (!data.substring(5).matches("^[0-9]+$")) {
                        rpm_bl = 0;
                        System.out.println("rpm bl: nan");
                    } else {
                        rpm_bl = (int) Double.parseDouble(data.substring(5));
                        System.out.println("rpm bl: " + rpm_bl);
                    }

                     mainController.setGaugeRpmbl(rpm_bl);
                    break;
                case "CMD08":
                    if (!data.substring(5).matches("^[0-9]+$")) {
                        rpm_br = 0;
                        System.out.println("rpm br: nan");
                    } else {
                        rpm_br = (int) Double.parseDouble(data.substring(5));
                        System.out.println("rpm br: " + rpm_br);
                    }

                    mainController.setGaugeRpmbr(rpm_br);
                    break;
                case "CMD09":
                    if (!data.substring(5).matches("^[0-9]+$")) {
                        System.out.println("Should never reach here");
                    } else {
                        state = (int) Double.parseDouble(data.substring(5));
                        System.out.println("state: " + state);
                    }

//                    mainController.setGaugeState(state);
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
