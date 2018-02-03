import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server for Mission Control
 * Telemetry sub-team, HypED 2018
 * The University of Edinburgh
 *
 * Launches front-end. Listens for a connection and when
 * TCP connection has been established with the pod, a PodThread
 * is started.
 * Sends data to the SpaceX server via UDP datagrams.
 *
 */
public class Server {

    // Our server is a TCP server that runs on port 5659
    private static final int portNo = 5695;
//    private static final int spaceXPortNo = 3000;
//    private static final String spaceXIP = "placeholder";
    private static ServerSocket serverSocket;
    private static Socket podSocket;

    /**
     * Runs the server.
     */
    public static void main(String[] args) {

        try {

            // TODO: Initialise front end

            serverSocket =



        } catch (Exception e) {
            // do something
        }

    }

    public static void sendToPod(Socket socket, String message) {
        // TODO: To be implemented
    }

    public static void sendConfirmationToPod(Socket socket) {
        // TODO: To be implemented
    }

    public static void sendToSpaceX(byte status, byte team_id,
                                    int acceleration, int position, int velocity) {
        // TODO: To be implemented
    }

    // PodThread


}
