import view.main.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;

public class DummyServer {

    private static final int PORT = 5695;
    private ServerSocket serverSocket;
    private Socket podSocket;
    private PrintWriter printWriter;
    private Scanner scanner;

    public DummyServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            podSocket = serverSocket.accept();

            scanner = new Scanner(podSocket.getInputStream());
            printWriter = new PrintWriter(podSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Start server.");
        int PORT = 5695;
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket podSocket = serverSocket.accept();
            System.out.println("Socket accepted");
//            Scanner scanner = new Scanner(podSocket.getInputStream());
//            PrintWriter printWriter = new PrintWriter(podSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
