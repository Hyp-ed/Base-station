import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

            if (confirmation.equals("1")) {
                System.out.println("Confirmation received.");
                break;
            }
        }
    }
}
