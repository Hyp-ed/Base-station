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
        cmdHashMap.put("CMD01", "state");                     //State stuff
        cmdHashMap.put("CMD02", "bat module status");
        cmdHashMap.put("CMD03", "nav module status");
        cmdHashMap.put("CMD04", "sen module status");
        cmdHashMap.put("CMD05", "motor module status");
        cmdHashMap.put("CMD06", "distance");                  //Nav stuff
        cmdHashMap.put("CMD07", "velocity");
        cmdHashMap.put("CMD08", "acceleration");
        cmdHashMap.put("CMD09", "hp voltage");                //HP Battery stuff
        cmdHashMap.put("CMD10", "hp current");
        cmdHashMap.put("CMD11", "hp charge");
        cmdHashMap.put("CMD12", "hp temp");
        cmdHashMap.put("CMD13", "hp low cell");
        cmdHashMap.put("CMD14", "hp high cell");
        cmdHashMap.put("CMD15", "hp voltage1");               //HP Battery1 stuff
        cmdHashMap.put("CMD16", "hp current1");
        cmdHashMap.put("CMD17", "hp charge1");
        cmdHashMap.put("CMD18", "hp temp1");
        cmdHashMap.put("CMD19", "hp low cell1");
        cmdHashMap.put("CMD20", "hp high cell1");
        cmdHashMap.put("CMD21", "lp voltage");                //LP Battery stuff
        cmdHashMap.put("CMD22", "lp current");
        cmdHashMap.put("CMD23", "lp charge");
        cmdHashMap.put("CMD24", "lp voltage1");
        cmdHashMap.put("CMD25", "lp current1");
        cmdHashMap.put("CMD26", "lp charge1");
        cmdHashMap.put("CMD27", "rpm fl");                    //Motor stuff
        cmdHashMap.put("CMD28", "rpm fr");
        cmdHashMap.put("CMD29", "rpm bl");
        cmdHashMap.put("CMD30", "rpm br");
        cmdHashMap.put("CMD31", "imu");                       //Sensor stuff
        cmdHashMap.put("CMD32", "embrakes");
        cmdHashMap.put("CMD33", "proxi front");
        cmdHashMap.put("CMD34", "proxi rear");

        // Map command codes to their corresponding LOWER threshold values (considered dangerous to drop below)
        lowerThresHashMap.put("CMD09", 72);  // HP voltage
        lowerThresHashMap.put("CMD15", 72);  // HP voltage 1
        lowerThresHashMap.put("CMD21", 13);  // LP voltage
        lowerThresHashMap.put("CMD24", 13);  // LP voltage 1

        // Map command codes to their corresponding UPPER threshold values (considered dangerous to go beyond)
        //upperThresHashMap.put("CMD01", 100); no threshold for state
        //upperThresHashMap.put("CMD02", 100); no threshold for module status
        //upperThresHashMap.put("CMD03", 100); no threshold for module status
        //upperThresHashMap.put("CMD04", 100); no threshold for module status
        //upperThresHashMap.put("CMD05", 100); no threshold for module status
        upperThresHashMap.put("CMD06", 1250);
        upperThresHashMap.put("CMD07", 90);
        upperThresHashMap.put("CMD08", 20);
        upperThresHashMap.put("CMD09", 120);
        upperThresHashMap.put("CMD10", 1500);
        upperThresHashMap.put("CMD11", 100);
        upperThresHashMap.put("CMD12", 70);
        upperThresHashMap.put("CMD13", 3600);
        upperThresHashMap.put("CMD14", 3600);
        upperThresHashMap.put("CMD15", 120);
        upperThresHashMap.put("CMD16", 1500);
        upperThresHashMap.put("CMD17", 100);
        upperThresHashMap.put("CMD18", 70);
        upperThresHashMap.put("CMD19", 3600);
        upperThresHashMap.put("CMD20", 3600);
        upperThresHashMap.put("CMD21", 30);
        upperThresHashMap.put("CMD22", 30000);
        upperThresHashMap.put("CMD23", 100);
        upperThresHashMap.put("CMD24", 30);
        upperThresHashMap.put("CMD25", 30000);
        upperThresHashMap.put("CMD26", 100);
        upperThresHashMap.put("CMD27", 6400);
        upperThresHashMap.put("CMD28", 6400);
        upperThresHashMap.put("CMD29", 6400);
        upperThresHashMap.put("CMD30", 6400);
        //upperThresHashMap.put("CMD31", 100); no threshold for sensors
        //upperThresHashMap.put("CMD32", 100); no threshold for sensors
        //upperThresHashMap.put("CMD33", 100); no threshold for sensors
        //upperThresHashMap.put("CMD34", 100); no threshold for sensors
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
