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
    private static final int SPACE_X_PORT = 3000;
    public static final int ACK_FROM_SERVER = 4;

    private ServerSocket serverSocket;
    private Socket podSocket;
    private PrintWriter printWriter;
    private Scanner scanner;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
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
            scanner.close();
            printWriter.close();
        }
    }

    private void startCommunication() {
        if (podSocket == null) {
            System.err.println("ERROR: NO POD SOCKET");
            // TODO: exit system
        }

        int distance, velocity, acceleration, stripe_count,
                rpm_fl, rpm_fr, rpm_br, rpm_bl;
        String data;

        while (scanner.hasNext()) {
            data = scanner.nextLine();

            switch(data.substring(0, 5)) {
                case "CMD01":
                    distance = (int) Double.parseDouble(data.substring(5));
                    System.out.println("distance: " + distance);
                    //sendHandshakeToPod();
                    break;
                case "CMD02":
                    velocity = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("velocity: " + velocity);
                    //sendHandshakeToPod();
                    break;
                case "CMD03":
                    acceleration = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("acceleration: " + acceleration);
                    //sendHandshakeToPod();
                    break;
                case "CMD04":
                    stripe_count = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("stripe count: " + stripe_count);
                    //sendHandshakeToPod();
                    break;
                case "CMD05":
                    rpm_fl = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("rpm fl: " + rpm_fl);
                    //sendHandshakeToPod();
                    break;
                case "CMD06":
                    rpm_fr = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("rpm fr: " + rpm_fr);
                    //sendHandshakeToPod();
                    break;
                case "CMD07":
                    rpm_bl = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("rpm bl: " + rpm_bl);
                    //sendHandshakeToPod();
                    break;
                case "CMD08":
                    rpm_br = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("rpm br: " + rpm_br);
                    //sendHandshakeToPod();
                    break;
            }
            //sendToSpaceX();
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

//    public void sendHandshakeToPod() {
//        if (podSocket == null) {
//            System.out.println("ERROR: no pod found");
//            return;
//        }
//
//        PrintWriter printWriter = null;
//        try {
//            printWriter = new PrintWriter(podSocket.getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        printWriter.println(1);
//        printWriter.flush();
//        System.out.println("hand");
//    }

    public static void sendToSpaceX(byte status, byte team_id,
                                    int acceleration, int position, int velocity) {
        try {

            DatagramSocket spaceXSocket = new DatagramSocket(SPACE_X_PORT);
            ByteBuffer buf = ByteBuffer.allocate(34); // BigEndian by default
            buf.put(team_id);
            buf.put(status);
            buf.putInt(acceleration);
            buf.putInt(position);
            buf.putInt(velocity);
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(0);
            buf.putInt(0);
            InetAddress IP =  InetAddress.getByName(/*_spaceXIP*/"192.168.1.163");
            DatagramPacket packet = new DatagramPacket(buf.array(), buf.limit(),
                    IP, SPACE_X_PORT);
            spaceXSocket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
