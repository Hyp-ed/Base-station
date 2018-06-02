
import java.io.*;
import java.net.*;

public class UDPtest {

    public static void main(String[] args) throws IOException {

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket(4445);
        byte[] buf = new byte[40];
        System.out.println("UDP client started");

        while(true) {
            // get response
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            System.out.println("Packet received");

            // display response
//            String received = new String(packet.getData(), packet.getOffset(), packet.getLength());
//            System.out.println(received);
            int len = buf.length;
            for (int i = 0; i < len; i++) {
                System.out.println(buf[i]);
            }
        }
    }

}
