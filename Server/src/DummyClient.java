import view.main.Server;

import java.io.*;
import java.net.Socket;

/**
 * Dummy client class to check the implementation of the Server class
 */
public class DummyClient {

    private static final int PORT = 5695;

    public static void main(String[] args) throws IOException {
        // IP address of a machine that is listening for a TCP connection request on port 5695
        String serverAddress = "localhost";

        System.out.println("Client request connection with server with server address: " + serverAddress + " at port number: " + PORT);
        Socket podSocket = new Socket(serverAddress, PORT);

        BufferedReader serverBuffer = new BufferedReader(new InputStreamReader(podSocket.getInputStream()));
        while (true) {
            String confirmation = serverBuffer.readLine();
            if (confirmation.equals(Server.ACK_FROM_SERVER)) {
                System.out.println("Received confirmation from server");
                while (true) {
                    String serverData = serverBuffer.readLine();
                    if (serverData != null) {
                        System.out.println(String.format("Received %s from server", serverData));
                    }
                }
            }
        }
    }
}
