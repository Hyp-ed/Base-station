import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        private Scanner is = null;
        private PrintWriter os = null;
        String data = "";
        
        public PodThread(Socket podSocket) {
            this.podSocket = podSocket;
        }
        
        public void run(){
        
            try {
                //opens input and output streams on the podsocket
                is = new Scanner(podSocket.getInputStream());
                os = new PrintWriter(podSocket.getOutputStream());
                
                int acceleration = 0, temperature = 0, stripe_count = 0, 
                        distance = 0, position = 0, velocity = 0, 
                        ground_proximity =0, battery_voltage = 0, 
                        battery_current = 0, battery_temperature = 0, 
                        pod_temperature = 0, rail_proximity=0, pump1 =0,
                        pump2 =0, accumulator1 =0, accumulator2 = 0; 
                
                while(true){
                
                    //whilst there is a connection any data that is sent is identified by its CMD code, saved and outputted 
                    if (!is.hasNext()) return;
                    data = is.nextLine();
                    switch(data.substring(0, 5))
                        {
                              case "CMD01":
                                sendConfirmationToPod(podSocket);
                                acceleration = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("acceleration: "+acceleration);
                                break;
                              case "CMD02":
                                sendConfirmationToPod(podSocket);
                                velocity = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("velocity: "+velocity);
                                break;
                              case "CMD03":
                                sendConfirmationToPod(podSocket);
                                position = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("position: "+position);
                                break;
                              case "CMD04":
                                sendConfirmationToPod(podSocket);
                                temperature = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("temperature: "+temperature);
                                break;
                              case "CMD05":
                                sendConfirmationToPod(podSocket);
                                stripe_count = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("stripe count: "+stripe_count);
                                break;
                              case "CMD06":
                                sendConfirmationToPod(podSocket);
                                distance = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("distance: "+distance);
                                break;
                              case "CMD07":
                                sendConfirmationToPod(podSocket);
                                ground_proximity = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("ground proximity: "+ground_proximity);
                                break;
                              case "CMD08":
                                sendConfirmationToPod(podSocket);
                                rail_proximity = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("rail proxmity: "+rail_proximity);
                                break;
                              case "CMD09":
                                sendConfirmationToPod(podSocket);
                                battery_temperature = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("battery temperature: "+battery_temperature);
                                break;
                              case "CMD10":
                                sendConfirmationToPod(podSocket);
                                battery_current = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("current: "+battery_current);
                                break;
                              case "CMD11":
                                sendConfirmationToPod(podSocket);
                                battery_voltage = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("voltage: "+battery_voltage);
                                break;
                              case "CMD12":
                                sendConfirmationToPod(podSocket);
                                accumulator1 = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("accumulator1: "+accumulator1);
                                break;
                              case "CMD13":
                                sendConfirmationToPod(podSocket);
                                accumulator2 = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("accumulator2: "+accumulator2);
                                break;
                              case "CMD14":
                                sendConfirmationToPod(podSocket);
                                pump1 = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("pump1: "+pump1);
                                break;
                              case "CMD15":
                                sendConfirmationToPod(podSocket);
                                pump2 = 
                                        (int) Math.round(Double.parseDouble
                                        (
                                                data.substring(5))
                                        );
                                System.out.println("pump2: "+pump2);
                                break;
                                                           
                        }
                }
                
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
