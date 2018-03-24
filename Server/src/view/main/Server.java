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
    public static final String ACK_FROM_SERVER = "1";

    private ServerSocket serverSocket;
    private Socket podSocket;

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
            sendToPod(ACK_FROM_SERVER);
            startCommunication();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startCommunication() throws IOException {
        Scanner is = new Scanner(podSocket.getInputStream());
        PrintWriter os = new PrintWriter(podSocket.getOutputStream());

        int distance = 0, velocity = 0, acceleration = 0,
                stripe_count = 0, rpm_fl = 0,
                rpm_fr = 0, rpm_br = 0, rpm_bl;

        while (true) {
            if (!is.hasNext()) {
                continue;
            }

            String data = is.nextLine();

            switch(data.substring(0, 5)) {
                case "CMD01":

                    distance = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("distance: " + distance);
                    break;
                case "CMD02":

                    velocity = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("velocity: " + velocity);
                    break;
                case "CMD03":

                    acceleration = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("acceleration: " + acceleration);
                    break;
                case "CMD04":

                    stripe_count = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("stripe count: " + stripe_count);
                    break;
                case "CMD05":

                    rpm_fl = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("rpm fl: " + rpm_fl);
                    break;
                case "CMD06":

                    rpm_fr = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("rpm fr: " + rpm_fr);
                    break;
                case "CMD07":

                    rpm_bl = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("rpm bl: " + rpm_bl);
                    break;
                case "CMD08":

                    rpm_br = (int) Math.round(Double.parseDouble(data.substring(5)));
                    System.out.println("rpm br: " + rpm_br);
                    break;
            }
                //sendToSpaceX();
            }
    }

    public void sendToPod(String message) {
        if (podSocket == null) {
            System.out.println("ERROR: no pod found");
            return;
        }

        System.out.println(String.format("Sending %s to pod", message));
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(podSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        printWriter.println(message);
        printWriter.flush();
    }

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
