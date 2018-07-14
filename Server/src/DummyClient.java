import view.main.Server;

import java.io.*;
import java.net.Socket;

/**
 * Dummy client class to check the implementation of the Server class
 */
public class DummyClient {

    private static final int PORT = 5695;

    public static void main(String[] args) throws IOException, InterruptedException {
        // IP address of a machine that is listening for a TCP connection request on port 5695
        String serverAddress = "localhost";

        System.out.println("Client request connection with server with server address: " + serverAddress + " at port number: " + PORT);
        Socket podSocket = new Socket(serverAddress, PORT);

        //A: Receive test
        while (true) {
            BufferedReader input = new BufferedReader(new InputStreamReader(podSocket.getInputStream()));
            String confirmation = input.readLine();
            PrintStream dummydata = new PrintStream(podSocket.getOutputStream());

            if (confirmation.equals("0")) {
                System.out.println("Confirmation received.");

                while(true) {
                    for(int x=0; x<100; x++){
                        dummydata.println("CMD01"+Integer.toString(x*2));
                        dummydata.println("CMD02"+Integer.toString(x));
                        dummydata.println("CMD03-2");
                        dummydata.println("CMD04"+Integer.toString(x));
                        dummydata.println("CMD05"+Integer.toString(x));
                        dummydata.println("CMD06"+Integer.toString(x));
                        dummydata.println("CMD07"+Integer.toString(x));
                        dummydata.println("CMD0803");
                        dummydata.println("CMD09"+Integer.toString(x));
                        dummydata.println("CMD11"+Integer.toString(x));
                        dummydata.println("CMD12"+Integer.toString(x));
                        dummydata.println("CMD14"+Integer.toString(x));
                        dummydata.println("CMD15"+Integer.toString(x));
                        dummydata.println("CMD16"+Integer.toString(x));
                        dummydata.println("CMD171111");
                        dummydata.println("CMD1811111111");
                        dummydata.println("CMD1911111111");
                        dummydata.println("CMD2011");
                        Thread.sleep(200);
                    }
                }

            }
        }
        //End of A

        //B: Send test
//        BufferedReader serverBuffer = new BufferedReader(new InputStreamReader(podSocket.getInputStream()));
//        while (true) {
//            String confirmation = serverBuffer.readLine();
//            if (confirmation.equals("0")) {
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
