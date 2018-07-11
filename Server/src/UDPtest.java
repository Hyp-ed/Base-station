
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class UDPtest {

    public static void main(String[] args) throws IOException {

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket(4445);
        byte[] buf = new byte[34];
        System.out.println("UDP client started");

        while(true) {
            // get response
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            // displace response
            System.out.println("Packet received");
            System.out.println(buf[0]);  // team id
            System.out.println(buf[1]);  // status
            for (int i = 2; i < buf.length; i+=4) {
                int reading = ByteBuffer.wrap(buf, i, 4).slice().getInt();
                System.out.println(reading);
            }
        }
    }

}
