package com.app.vortex.vortex.app;

import android.content.SharedPreferences;

/**
 * Created by Kasun on 1/6/2017.
 */

public class SettingsHelper {
    private int user_id;
    private int notifications;
    private int auto_clean;
    private int auto_clean_interval;
    private int auto_fill_tank;
    private int notify_on_level;
    private int tank_full_alert;
    private int well_critical_alert;
    private int cleaning_completed_alert;
    private int tank_empty_alert;

    public SettingsHelper() {
    }

    public int getUser_id() {
        return user_id;
    }

    public boolean getNotifications() {
        return getBool(notifications);
    }

    public boolean getAuto_clean() {
        return getBool(auto_clean);
    }

    public int getAuto_clean_interval() {
        return auto_clean_interval;
    }

    public boolean getAuto_fill_tank() {
        return getBool(auto_fill_tank);
    }

    public int getNotify_on_level() {
        return notify_on_level;
    }

    public boolean getTank_full_alert() {
        return getBool(tank_full_alert);
    }

    public boolean getWell_critical_alert() {
        return getBool(well_critical_alert);
    }

    public boolean getCleaning_completed_alert() {
        return getBool(cleaning_completed_alert);
    }

    public boolean getTank_empty_alert() {
        return getBool(tank_empty_alert);
    }

    private boolean getBool(int i){
        return (i == 1 ? true : false);
    }

    public void saveSettings(SharedPreferences.Editor editor){
        editor
                .putInt(Vortex.USER_ID, getUser_id())
                .putBoolean(Vortex.KEY_AUTO_FILL_TANK, getAuto_fill_tank())
                .putBoolean(Vortex.KEY_AUTOCLEAN, getAuto_clean())
                .putInt(Vortex.KEY_AUTOCLEAN_INTERVAL,getAuto_clean_interval())
                .putInt(Vortex.KEY_NOTIFY_ME, getNotify_on_level())
                .putBoolean(Vortex.KEY_TANK_FULL, getTank_full_alert())
                .putBoolean(Vortex.KEY_TANK_EMPTY, getTank_empty_alert())
                .putBoolean(Vortex.KEY_CLEANING_COMPLETED, getCleaning_completed_alert())
                .putBoolean(Vortex.KEY_NOTIFICATIONS, getNotifications())
                .putBoolean(Vortex.KEY_WELL_EMPTY, getWell_critical_alert())
                .apply();
    }
}