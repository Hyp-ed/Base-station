package view.main;

import java.util.HashMap;

/**
 * Class for command code and threshold settings.
 *
 * @author: Kofi and Isa, HYPED 17/18
 */
public class Util {

    private static HashMap<String, String> cmdHashMap = new HashMap<String, String>();
    private static HashMap<String, Integer> lowerThresHashMap = new HashMap<>();
    private static HashMap<String, Integer> upperThresHashMap = new HashMap<String, Integer>();

    static {
        // Maps command codes to data names
        cmdHashMap.put("CMD01", "distance");
        cmdHashMap.put("CMD02", "velocity");
        cmdHashMap.put("CMD03", "acceleration");
        cmdHashMap.put("CMD04", "rpm fl");
        cmdHashMap.put("CMD05", "rpm fr");
        cmdHashMap.put("CMD06", "rpm bl");
        cmdHashMap.put("CMD07", "rpm br");
        cmdHashMap.put("CMD08", "state");
        cmdHashMap.put("CMD09", "imu");             // sensors
        cmdHashMap.put("CMD10", "proxi front");
        cmdHashMap.put("CMD11", "proxi rear");
        cmdHashMap.put("CMD12", "emergency brakes");
        cmdHashMap.put("CMD13", "hp volt");         // High power battery
        cmdHashMap.put("CMD14", "hp current");
        cmdHashMap.put("CMD15", "hp charge");
        cmdHashMap.put("CMD16", "hp temp");
        cmdHashMap.put("CMD17", "hp low cell");
        cmdHashMap.put("CMD18", "hp high cell");
        cmdHashMap.put("CMD19", "hp volt1");        // High power battery 1
        cmdHashMap.put("CMD20", "hp current1");
        cmdHashMap.put("CMD21", "hp charge1");
        cmdHashMap.put("CMD22", "hp temp1");
        cmdHashMap.put("CMD23", "hp low cell1");
        cmdHashMap.put("CMD24", "hp high cell1");
        cmdHashMap.put("CMD25", "lp voltage");      // Low power battery
        cmdHashMap.put("CMD26", "lp current");
        cmdHashMap.put("CMD27", "lp charge");
        cmdHashMap.put("CMD28", "lp voltage1");     // Low power battery 1
        cmdHashMap.put("CMD29", "lp current1");
        cmdHashMap.put("CMD30", "lp charge1");

        // Map command codes to their corresponding LOWER threshold values (considered dangerous to drop below)
        lowerThresHashMap.put("CMD13", 72);  // HP voltage
        lowerThresHashMap.put("CMD19", 72);  // HP voltage 1
        lowerThresHashMap.put("CMD25", 13);  // LP voltage
        lowerThresHashMap.put("CMD28", 13);  // LP voltage 1

        // Map command codes to their corresponding UPPER threshold values (considered dangerous to go beyond)
        upperThresHashMap.put("CMD01", 1250);
        upperThresHashMap.put("CMD02", 90);
        upperThresHashMap.put("CMD03", 20);
        upperThresHashMap.put("CMD04", 6400);
        upperThresHashMap.put("CMD05", 6400);
        upperThresHashMap.put("CMD06", 6400);
        upperThresHashMap.put("CMD07", 6400);
//        upperThresHashMap.put("CMD08", 100); // no threshold for state
//        upperThresHashMap.put("CMD09", 0);   // no threshold for sensors
//        upperThresHashMap.put("CMD10", 0);
//        upperThresHashMap.put("CMD11", 0);
//        upperThresHashMap.put("CMD12", 0);
        upperThresHashMap.put("CMD13", 120);    // HIGH POWER voltage
        upperThresHashMap.put("CMD14", 1500);   // current
        upperThresHashMap.put("CMD15", 100);    // charge
        upperThresHashMap.put("CMD16", 70);     // temperature
        upperThresHashMap.put("CMD17", 3600);      // low cell
        upperThresHashMap.put("CMD18", 3600);      // high cell
        upperThresHashMap.put("CMD19", 120);    // HIGH POWER voltage 1
        upperThresHashMap.put("CMD20", 1500);   // current 1
        upperThresHashMap.put("CMD21", 100);    // charge 1
        upperThresHashMap.put("CMD22", 70);     // temperature 1
        upperThresHashMap.put("CMD23", 3600);      // low cell 1
        upperThresHashMap.put("CMD24", 3600);      // high cell 1
        upperThresHashMap.put("CMD25", 30);     // LOW POWER voltage
        upperThresHashMap.put("CMD26", 30000);     // current
        upperThresHashMap.put("CMD27", 100);    // charge
        upperThresHashMap.put("CMD28", 30);     // LOW POWER voltage 1
        upperThresHashMap.put("CMD29", 30000);     // current 1
        upperThresHashMap.put("CMD30", 100);    // charge 1
    }

    public static String getNameByCmdCode(String cmdString) {
        return cmdHashMap.get(cmdString);
    }

    public static boolean isLowerThresKey(String cmdString) {
        return lowerThresHashMap.containsKey(cmdString);
    }

    public static int getLowerThresByCmdCode(String cmdString) {
        return lowerThresHashMap.get(cmdString);
    }

    public static int getUpperThresByCmdCode(String cmdString) {
        return upperThresHashMap.get(cmdString);
    }
}
