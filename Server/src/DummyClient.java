import java.io.*;
import java.net.Socket;

/**
 * Dummy client class to check the implementation of the Server class
 */
public class DummyClient {

    private static final int portNo = 5695;

    public static void main(String[] args) throws IOException {
        // IP address of a machine that is listening for a TCP connection request on port 5695
        String serverAddress = "localhost";

        System.out.println("Client request connection with server with server address: "+serverAddress+" at port number: "+portNo);
        Socket podSocket = new Socket(serverAddress, portNo);

        while (true) {
            BufferedReader input = new BufferedReader(new InputStreamReader(podSocket.getInputStream()));
            String confirmation = input.readLine();
            PrintStream dummydata = new PrintStream(podSocket.getOutputStream());

            if (confirmation.equals("1")) {
                System.out.println("Confirmation received.");
                
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
                
                break;
            }
        }
    }
}
