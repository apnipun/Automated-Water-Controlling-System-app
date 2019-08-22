package com.app.vortex.vortex.app;

import java.util.HashMap;

/**
 * Created by Nipun on 11/27/2016.
 * Stores basic settings and functionality for the app
 */

public class Vortex {
    //Settings
    public static final String KEY_TANK_FULL = "pref_tank_full";
    public static final String KEY_TANK_EMPTY = "pref_tank_empty";
    public static final String KEY_CLEANING_COMPLETED = "pref_notify_on_cleaning_completed";
    public static final String KEY_NOTIFICATIONS = "pref_notification_on";
    public static final String KEY_VIBRATE = "pref_vibrate";
    public static final String KEY_PHONE_LED = "pref_phone_led";
    public static final String KEY_AUTOCLEAN = "pref_autoclean";
    public static final String KEY_AUTOCLEAN_INTERVAL = "pref_auto_clean_update_interval";
    public static final String PREF_REFRESH_INTERVAL = "pref_refresh_interval";
    public static final String KEY_NOTIFY_ME = "pref_notify_me";
    public static final String KEY_TONE = "pref_tone";
    public static final String KEY_WELL_EMPTY = "pref_notify_on_well_empty";
    public static final String KEY_AUTO_FILL_TANK = "pref_auto_fill_tank";

    public static final String DEVICE_ID = "device_id";
    public static final String USER_ID = "user_id";
    //end settings

    //pubic host address,change to your IP or set to your domain
    //example : HOST_ADDRESS = "http://www.vortex.io/";
    public static final String HOST_ADDRESS = "https://vortexapi.000webhostapp.com/";

    //for GCM service
    public static final String TAG_ONEOFF_UPDATE_TASK = "com.vortex.vortex.oneoff.update.task";
    public static final String TAG_PERIODIC_UPDATE_TASK = "com.vortex.vortex.periodic.update.task";

    public static final HashMap<String, String> Values;
    static {
        Values = new HashMap<>();
        Values.put("d", "day");
        Values.put("w", "week");
        Values.put("mo", "month");
        Values.put("y", "year");
        Values.put("s", "second");
        Values.put("mi", "minute");
    }

    public static final HashMap<String, Integer> ReflexPeriods;
    static {
        ReflexPeriods = new HashMap<>();
        ReflexPeriods.put("2,s", 1);
        ReflexPeriods.put("5,s", 2);
        ReflexPeriods.put("10,s", 3);
    }

    //methods

    //@value = value in seconds/minutes as a string
    public static int valueToSeconds(String value) {
        int val = 0;
        int multiple = 1;
        String[] v = value.split(",");

        if (v[1] == "mi")
            multiple = 60;

        val = Integer.parseInt(v[0]) * multiple;
        return val;
    }

    //this will convert a auto clean interval to days
    public static int valueToDays(String value){
        int days = 0;
        int mult = 1;

        String[] v = value.split(",");
        String m = v[1];

        if(m == "w")
            mult = 7;
        else if(m == "mo")
            mult = 30;
        else if(m == "y")
            mult = 365;

        days = Integer.parseInt(v[0]) * mult;

        return days;
    }
}
