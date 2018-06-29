package view.main;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Class for command code and threshold settings.
 *
 * @author: HYPED
 */
public class Util {

    private static HashMap<String, String> cmdHashMap = new HashMap<String, String>();
    private static HashMap<String, Integer> thresHashMap = new HashMap<String, Integer>();

    static {
        // Maps command codes to data names
        cmdHashMap = new HashMap();
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
        cmdHashMap.put("CMD17", "torque fr");
        cmdHashMap.put("CMD18", "torque fl");
        cmdHashMap.put("CMD19", "torque br");
        cmdHashMap.put("CMD20", "torque bl");
        cmdHashMap.put("CMD21", "imu");
        cmdHashMap.put("CMD22", "proxi front");
        cmdHashMap.put("CMD23", "proxi rear");

        // Map command codes to their corresponding threshold values (considered dangerous to go beyond)
        thresHashMap = new HashMap();
        thresHashMap.put("CMD01", 100);
        thresHashMap.put("CMD02", -1);
        thresHashMap.put("CMD03", 100);
        thresHashMap.put("CMD04", 100);
        thresHashMap.put("CMD05", 100);
        thresHashMap.put("CMD06", 100);
        thresHashMap.put("CMD07", 100);
//        thresHashMap.put("CMD08", 100); // no threshold for state (?)
        thresHashMap.put("CMD09", 100);
        thresHashMap.put("CMD10", 100);
        thresHashMap.put("CMD11", 100);
        thresHashMap.put("CMD12", 100);
        thresHashMap.put("CMD13", 100);
        thresHashMap.put("CMD14", 100);
        thresHashMap.put("CMD15", 100);
        thresHashMap.put("CMD16", 100);
        thresHashMap.put("CMD17", 100);
        thresHashMap.put("CMD18", 100);
        thresHashMap.put("CMD19", 100);
        thresHashMap.put("CMD20", 100);
        thresHashMap.put("CMD21", 100);
        thresHashMap.put("CMD22", 100);
        thresHashMap.put("CMD23", 100);
    }

    public static String getNameByCmdCode(String cmdString) {
        return cmdHashMap.get(cmdString);
    }

    public static int getThresByCmdCode(String cmdString) {
        return thresHashMap.get(cmdString);
    }
}
