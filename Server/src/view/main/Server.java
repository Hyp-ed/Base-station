package view.main;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
    private MainController mainController;


    int distance, velocity, acceleration, stripe_count,
            rpm_fl, rpm_fr, rpm_br, rpm_bl;
    String data;

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
            scanner.close();
            printWriter.close();
        }
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
                    if(data.substring(5).equals("-nan")){
                        distance = 0;
                        System.out.println("distance: nan");
                    }else {
                        distance = (int) Double.parseDouble(data.substring(5));
                        System.out.println("distance: " + distance);
                    }
                    mainController.setDistanceLabel(distance);

                    break;
                case "CMD02":
                    if(data.substring(5).equals("-nan")){
                        velocity = 0;
                        System.out.println("velocity: nan");
                    }else {
                        velocity = (int) Double.parseDouble(data.substring(5));
                        System.out.println("velocity: " + velocity);
                    }
                    mainController.setVelocityLabel(velocity);

                    break;
                case "CMD03":
                    if(data.substring(5).equals("-nan")){
                        acceleration = 0;
                        System.out.println("acceleration: nan");
                    }else {
                        acceleration = (int) Double.parseDouble(data.substring(5));
                        System.out.println("acceleration: " + acceleration);
                    }
                    mainController.setAccelerationLabel(acceleration);

                    break;
                case "CMD04":
                    if(data.substring(5).equals("-nan")){
                        stripe_count = 0;
                        System.out.println("stripe count: nan");
                    }else {
                        stripe_count = (int) Double.parseDouble(data.substring(5));
                        System.out.println("stripe count: " + stripe_count);
                    }
                    mainController.setStripeLabel(Integer.toString(stripe_count));

                    break;
                case "CMD05":
                    if(data.substring(5).equals("-nan")){
                        rpm_fl = 0;
                        System.out.println("rpm fl: nan");
                    }else {
                        rpm_fl = (int) Double.parseDouble(data.substring(5));
                        System.out.println("rpm fl: " + rpm_fl);
                    }

                    break;
                case "CMD06":
                    if(data.substring(5).equals("-nan")){
                        rpm_fr = 0;
                        System.out.println("rpm fr: nan");
                    }else {
                        rpm_fr = (int) Double.parseDouble(data.substring(5));
                        System.out.println("rpm fr: " + rpm_fr);
                    }

                    break;
                case "CMD07":
                    if(data.substring(5).equals("-nan")){
                        rpm_bl = 0;
                        System.out.println("rpm bl: nan");
                    }else {
                        rpm_bl = (int) Double.parseDouble(data.substring(5));
                        System.out.println("rpm bl: " + rpm_bl);
                    }


                    break;
                case "CMD08":
                    if(data.substring(5).equals("-nan")){
                        rpm_br = 0;
                        System.out.println("rpm br: nan");
                    }else {
                        rpm_br = (int) Double.parseDouble(data.substring(5));
                        System.out.println("rpm br: " + rpm_br);
                    }

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
