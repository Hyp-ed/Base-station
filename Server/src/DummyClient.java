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
                        dummydata.println("CMD091111");
                        dummydata.println("CMD1011111111");
                        dummydata.println("CMD1111111111");
                        dummydata.println("CMD1211");
                        dummydata.println("CMD13"+Integer.toString(x));
                        dummydata.println("CMD14"+Integer.toString(x));
                        dummydata.println("CMD15"+Integer.toString(x));
                        dummydata.println("CMD16"+Integer.toString(x));
                        dummydata.println("CMD17"+Integer.toString(x));
                        dummydata.println("CMD18"+Integer.toString(x));
                        dummydata.println("CMD19"+Integer.toString(x));
                        dummydata.println("CMD20"+Integer.toString(x));
                        dummydata.println("CMD21"+Integer.toString(x));
                        dummydata.println("CMD22"+Integer.toString(x));
                        dummydata.println("CMD23"+Integer.toString(x));
                        dummydata.println("CMD24"+Integer.toString(x));
                        dummydata.println("CMD25"+Integer.toString(x));
                        dummydata.println("CMD26"+Integer.toString(x));
                        dummydata.println("CMD27"+Integer.toString(x));
                        dummydata.println("CMD28"+Integer.toString(x));
                        dummydata.println("CMD29"+Integer.toString(x));
                        dummydata.println("CMD30"+Integer.toString(x));
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
