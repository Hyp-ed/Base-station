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

    // Our server is a TCP server that listens on port 5659
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

            serverSocket = new ServerSocket(portNo);
            // Awaits ping from pod

            // Initialise connection with pod's microcontroller
            while (true) {
                // Waits for connection request from pod
                podSocket = serverSocket.accept();
                // Connection established

                sendToPod(podSocket, "Ping"); // pings pod
                PodThread pod = new PodThread(podSocket);
                pod.start();
            }

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

    /**
     * Private PodThread class
     */
    private static class PodThread extends Thread {

        Socket podSocket;

        public PodThread(Socket podSocket) {
            this.podSocket = podSocket;
        }
    }

}
