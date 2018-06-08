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
                    for(int x=0; x<150; x++){
                        dummydata.println("CMD01"+Integer.toString(x));
                        dummydata.println("CMD02"+Integer.toString(x));
                        dummydata.println("CMD03"+Integer.toString(x));
                        dummydata.println("CMD04"+Integer.toString(x));
                        dummydata.println("CMD05"+Integer.toString(x));
                        dummydata.println("CMD06"+Integer.toString(x));
                        dummydata.println("CMD07"+Integer.toString(x));
                        dummydata.println("CMD08"+Integer.toString(x));
                        dummydata.println("CMD10"+Integer.toString(x));
                        dummydata.println("CMD11"+Integer.toString(x));
                        dummydata.println("CMD12"+Integer.toString(x));
                        dummydata.println("CMD13"+Integer.toString(x));
                    }
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
