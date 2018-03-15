package view.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server extends Thread {

    private static final int PORT = 5695;
    private static final int SPACE_X_PORT = 3000;
    public static final String ACK_FROM_SERVER = "ACK_FROM_SERVER";

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

        while (true) {
            if (!is.hasNext()) {
                continue;
            }

            String data = is.nextLine();
            System.out.println(String.format("Received %s from pod", data));
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

}
