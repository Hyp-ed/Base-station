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

        //A: Receive test
        while (true) {
            BufferedReader input = new BufferedReader(new InputStreamReader(podSocket.getInputStream()));
            String confirmation = input.readLine();
            PrintStream dummydata = new PrintStream(podSocket.getOutputStream());

            if (confirmation.equals("4")) {
                System.out.println("Confirmation received.");

                while(true) {
                    dummydata.println("CMD0160");
                    dummydata.println("CMD0260");
                    dummydata.println("CMD0360");
                    dummydata.println("CMD0460");
                    dummydata.println("CMD0560");
                    dummydata.println("CMD0660");
                    dummydata.println("CMD0760");
                    dummydata.println("CMD0860");
                    dummydata.println("CMD0960");
                    dummydata.println("CMD1060");
                    dummydata.println("CMD1160");
                    dummydata.println("CMD1260");
                    dummydata.println("CMD1360");
                    dummydata.println("CMD0860");
                }

            }
        }
        //End of A

        //B: Send test
//        BufferedReader serverBuffer = new BufferedReader(new InputStreamReader(podSocket.getInputStream()));
//        while (true) {
//            String confirmation = serverBuffer.readLine();
//            if (confirmation.equals("4")) {
//                System.out.println("Received confirmation from server");
//                while (true) {
//                    String serverData = serverBuffer.readLine();
//                    if (serverData != null) {
//                        System.out.println(String.format("Received %s from server", serverData));
//                    }
//                }
//            }
//        }
        //End of B
    }
}
