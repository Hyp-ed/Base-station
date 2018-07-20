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
        cmdHashMap.put("CMD09", "hp volt");
        cmdHashMap.put("CMD10", "hp temp");
        cmdHashMap.put("CMD11", "hp charge");
        cmdHashMap.put("CMD12", "hp volt1");
        cmdHashMap.put("CMD13", "hp temp1");
        cmdHashMap.put("CMD14", "hp charge1");
        cmdHashMap.put("CMD15", "lp charge");
        cmdHashMap.put("CMD16", "lp charge1");
        cmdHashMap.put("CMD17", "imu");
        cmdHashMap.put("CMD18", "proxi front");
        cmdHashMap.put("CMD19", "proxi rear");
        cmdHashMap.put("CMD20", "emergency brakes");
        cmdHashMap.put("CMD21", "hp current");
        cmdHashMap.put("CMD22", "hp current1");
        cmdHashMap.put("CMD23", "lp current");
        cmdHashMap.put("CMD24", "lp current1");


        // Map command codes to their corresponding LOWER threshold values (considered dangerous to drop below)
        lowerThresHashMap.put("CMD09", 72);  // HP voltage
        lowerThresHashMap.put("CMD12", 72);  // HP voltage 1

        // Map command codes to their corresponding UPPER threshold values (considered dangerous to go beyond)
        upperThresHashMap.put("CMD01", 1250);
        upperThresHashMap.put("CMD02", 90);
        upperThresHashMap.put("CMD03", 20);
        upperThresHashMap.put("CMD04", 6400);
        upperThresHashMap.put("CMD05", 6400);
        upperThresHashMap.put("CMD06", 6400);
        upperThresHashMap.put("CMD07", 6400);
//        upperThresHashMap.put("CMD08", 100); // no threshold for state (?)
        upperThresHashMap.put("CMD09", 120);
        upperThresHashMap.put("CMD10", 70);
        upperThresHashMap.put("CMD11", 100);
        upperThresHashMap.put("CMD12", 120);
        upperThresHashMap.put("CMD13", 70);
        upperThresHashMap.put("CMD14", 100);
        upperThresHashMap.put("CMD15", 100);
        upperThresHashMap.put("CMD16", 100);
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
