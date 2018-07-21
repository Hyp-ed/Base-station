package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class UdpSpacexTest {

    private static final int PORT = 5695;

    public static void main(String[] args) throws IOException {
        String serverAddress = "localhost";

        System.out.println("Client request connection with server.");
        Socket podSocket = new Socket(serverAddress, PORT);

        while (true) {
            BufferedReader input = new BufferedReader(new InputStreamReader(podSocket.getInputStream()));
            String confirmation = input.readLine();

            PrintStream dummydata = new PrintStream(podSocket.getOutputStream());

            if (confirmation.equals("2")) {
                System.out.println("Confirmation received.");
                System.out.println("Test 1: Values from 0 to 10.");

                for (int x=-1000; x<1000; x++) {
                    dummydata.println("CMD01"+Integer.toString(x % 10)); // state
                    dummydata.println("CMD02"+Integer.toString(x % 3)); // bat mod stat
                    dummydata.println("CMD03"+Integer.toString(x % 3)); // nav mod stat
                    dummydata.println("CMD04"+Integer.toString(x % 3)); // sen mod stat
                    dummydata.println("CMD05"+Integer.toString(x % 3)); // mtr mod stat
                    dummydata.println("CMD06"+Integer.toString(x % 1250)); // dist
                    dummydata.println("CMD07"+Integer.toString(x % 70)); // vel
                    dummydata.println("CMD08"+Integer.toString(x % 10)); // accel
                    dummydata.println("CMD09"+Integer.toString(x % 100)); // hp volt
                    dummydata.println("CMD10"+Integer.toString(x % 100)); // hp current
                    dummydata.println("CMD11"+Integer.toString(x % 100)); // hp charge
                    dummydata.println("CMD12"+Integer.toString(x % 100)); // hp temp
                    dummydata.println("CMD13"+Integer.toString(x % 100)); // hp lowest cell
                    dummydata.println("CMD14"+Integer.toString(x % 100)); // hp highest cell
                    dummydata.println("CMD15"+Integer.toString(x % 100)); // hp volt 1
                    dummydata.println("CMD16"+Integer.toString(x % 100)); // hp current 1
                    dummydata.println("CMD17"+Integer.toString(x % 100)); // hp charge 1
                    dummydata.println("CMD18"+Integer.toString(x % 100)); // hp temp 1
                    dummydata.println("CMD19"+Integer.toString(x % 100)); // hp lowest cell 1
                    dummydata.println("CMD20"+Integer.toString(x % 100)); // hp highest cell 1
                    dummydata.println("CMD21"+Integer.toString(x % 100)); // lp volt
                    dummydata.println("CMD22"+Integer.toString(x % 100)); // lp current
                    dummydata.println("CMD23"+Integer.toString(x % 100)); // lp charge
                    dummydata.println("CMD24"+Integer.toString(x % 100)); // lp volt 1
                    dummydata.println("CMD25"+Integer.toString(x % 100)); // lp current 1
                    dummydata.println("CMD26"+Integer.toString(x % 100)); // lp charge 1
                    dummydata.println("CMD27"+Integer.toString(x)); // RPM FL
                    dummydata.println("CMD28"+Integer.toString(x)); // RPM FR
                    dummydata.println("CMD29"+Integer.toString(x)); // RPM BL
                    dummydata.println("CMD30"+Integer.toString(x)); // RPM BR
                    dummydata.println("CMD311111"); // imu
                    dummydata.println("CMD321111"); // em brakes
                    dummydata.println("CMD3311111111"); // proxi front
                    dummydata.println("CMD3411111111"); // proxi rear
                }
            }
        }
    }
}
