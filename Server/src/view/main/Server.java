package view.main;

import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;

import java.net.ServerSocket;

import java.net.Socket;

import java.util.concurrent.Callable;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import java.util.logging.Level;

import java.util.logging.Logger;



import javafx.beans.property.DoubleProperty;

import javafx.beans.property.SimpleDoubleProperty;



public class Server {



    public static final int DEFAULT_PORT = 5695;



    private final int port;

    private final ExecutorService exec;

    private final Logger logger ;



    private final DoubleProperty speed = new SimpleDoubleProperty(this,

            "speed", 0);



    public final DoubleProperty speedProperty() {

        return this.speed;

    }



    public final double getSpeed() {

        return this.speedProperty().get();

    }



    public final void setSpeed(final double speed) {

        this.speedProperty().set(speed);

    }



    public Server(int port) throws IOException {

        this.port = port;



        this.exec = Executors.newCachedThreadPool(runnable -> {

            // run thread as daemon:

            Thread thread = new Thread(runnable);

            thread.setDaemon(true);

            return thread;

        });



        this.logger = Logger.getLogger("Server");



        try {

            startListening();

        } catch (IOException exc) {

            exc.printStackTrace();

            throw exc;

        }

    }



    public Server() throws IOException {

        this(DEFAULT_PORT);

    }



    public void startListening() throws IOException {

        Callable<Void> connectionListener = () -> {

            try (ServerSocket serverSocket = new ServerSocket(port)) {

                logger.info(

                        "Server listening on " + serverSocket.getInetAddress()

                                + ":" + serverSocket.getLocalPort());

                while (true) {

                    logger.info( "Waiting for connection from pod:");

                    Socket socket = serverSocket.accept();

                    logger.info( "Connection accepted from " + socket.getInetAddress());

                    handleConnection(socket);

                }

            } catch (Exception exc) {

                logger.log(Level.SEVERE, "Exception in connection handler", exc);

            }

            return null;

        };

        exec.submit(connectionListener);

    }



    public void shutdown() {

        exec.shutdownNow();

    }



    private void handleConnection(Socket socket) {

        Callable<Void> connectionHandler = () -> {

            try (BufferedReader in = new BufferedReader(new InputStreamReader(

                    socket.getInputStream()))) {

                String line;

                while ((line = in.readLine()) != null) {

                    logger.info("Received: " + line);

                    processLine(line);

                }

                System.out.println("Connection closed from "+socket.getInetAddress());

            }

            return null;

        };

        exec.submit(connectionHandler);

    }



    private void processLine(String line) {

        if (line.substring(0, 5) == "CMD01") {

            try {

                speed.set(Double.parseDouble(line.substring(5)));

            } catch (NumberFormatException exc) {

                logger.log(Level.WARNING, "Non-numeric speed supplied", exc);

            }

        }

    }



}
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.net.*;
//import java.nio.ByteBuffer;
//import java.util.Scanner;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class Server extends Thread {
//
//    private static final int PORT = 5695;
//    private static final int SPACE_X_PORT = 3000;
//    public static final int ACK_FROM_SERVER = 4;
//
//    private ServerSocket serverSocket;
//    private Socket podSocket;
//
//    int distance = 10, velocity = 0, acceleration = 0,
//            stripe_count = 0, rpm_fl = 0,
//            rpm_fr = 0, rpm_br = 0, rpm_bl;
//
//    public Server() {
//        try {
//            serverSocket = new ServerSocket(PORT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void run() {
//        System.out.println("Awaiting connection from pod...");
//        try {
//            podSocket = serverSocket.accept();
//            sendToPod(ACK_FROM_SERVER);
//            startCommunication();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void startCommunication() throws IOException
//    {
//        Scanner is = new Scanner(podSocket.getInputStream());
//        PrintWriter os = new PrintWriter(podSocket.getOutputStream());
//
//
//        while (true) {
//            if (!is.hasNext()) {
//                continue;
//            }
//
//            String data = is.nextLine();
//
//            switch(data.substring(0, 5)) {
//                case "CMD01":
//
//                    distance = (int) Math.round(Double.parseDouble(data.substring(5)));
//                    System.out.println("distance: " + distance);
//                    sendHandshakeToPod();
//                    break;
//                case "CMD02":
//
//                    velocity = (int) Math.round(Double.parseDouble(data.substring(5)));
//                    System.out.println("velocity: " + velocity);
//                    sendHandshakeToPod();
//                    break;
//                case "CMD03":
//
//                    acceleration = (int) Math.round(Double.parseDouble(data.substring(5)));
//                    System.out.println("acceleration: " + acceleration);
//                    sendHandshakeToPod();
//                    break;
//                case "CMD04":
//
//                    stripe_count = (int) Math.round(Double.parseDouble(data.substring(5)));
//                    System.out.println("stripe count: " + stripe_count);
//                    sendHandshakeToPod();
//                    break;
//                case "CMD05":
//
//                    rpm_fl = (int) Math.round(Double.parseDouble(data.substring(5)));
//                    System.out.println("rpm fl: " + rpm_fl);
//                    sendHandshakeToPod();
//                    break;
//                case "CMD06":
//
//                    rpm_fr = (int) Math.round(Double.parseDouble(data.substring(5)));
//                    System.out.println("rpm fr: " + rpm_fr);
//                    sendHandshakeToPod();
//                    break;
//                case "CMD07":
//
//                    rpm_bl = (int) Math.round(Double.parseDouble(data.substring(5)));
//                    System.out.println("rpm bl: " + rpm_bl);
//                    sendHandshakeToPod();
//                    break;
//                case "CMD08":
//
//                    rpm_br = (int) Math.round(Double.parseDouble(data.substring(5)));
//                    System.out.println("rpm br: " + rpm_br);
//                    sendHandshakeToPod();
//                    break;
//            }
//                //sendToSpaceX();
//            }
//    }
//
//    public void sendToPod(int message) {
//        if (podSocket == null) {
//            System.out.println("ERROR: no pod found");
//            return;
//        }
//
//        System.out.println(String.format("Sending %s to pod", message));
//        PrintWriter printWriter = null;
//        try {
//            printWriter = new PrintWriter(podSocket.getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        printWriter.println(message);
//        printWriter.flush();
//    }
//
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
//    }
//
//    public static void sendToSpaceX(byte status, byte team_id,
//                                    int acceleration, int position, int velocity) {
//        try {
//
//            DatagramSocket spaceXSocket = new DatagramSocket(SPACE_X_PORT);
//            ByteBuffer buf = ByteBuffer.allocate(34); // BigEndian by default
//            buf.put(team_id);
//            buf.put(status);
//            buf.putInt(acceleration);
//            buf.putInt(position);
//            buf.putInt(velocity);
//            buf.putInt(0);
//            buf.putInt(0);
//            buf.putInt(0);
//            buf.putInt(0);
//            buf.putInt(0);
//            InetAddress IP =  InetAddress.getByName(/*_spaceXIP*/"192.168.1.163");
//            DatagramPacket packet = new DatagramPacket(buf.array(), buf.limit(),
//                    IP, SPACE_X_PORT);
//            spaceXSocket.send(packet);
//        } catch (IOException ex) {
//            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//
//}
