package com.app.vortex.vortex.fragments;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import android.util.Log;

import com.app.vortex.vortex.R;
import com.app.vortex.vortex.app.Vortex;

//Settings fragment
public class SettingFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.preference);

        //display all info on start
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
        //setIntervalPreference(sharedPreferences, Vortex.KEY_AUTOCLEAN_INTERVAL, "The system will " +
                //"clean up after", "an one");//for autoclean interval
        //setIntervalPreference(sharedPreferences, Vortex.PREF_REFRESH_INTERVAL, "The app will " +
                //"refresh in each", "");//for update

        //manual set ringintone pref change listener
        /*final Preference notificationTone = findPreference(Vortex.KEY_TONE);
        notificationTone.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String tone = (String) newValue;
                if (tone.isEmpty())
                    tone = "None";

                Ringtone ringtone = RingtoneManager.getRingtone(getContext(), Uri.parse(tone));
                preference.setSummary(ringtone.getTitle(getContext()));
                return true;
            }
        });

        String tone = sharedPreferences.getString(Vortex.KEY_TONE, "None");
        Ringtone ringtone = RingtoneManager.getRingtone(getContext(), Uri.parse(tone));
        notificationTone.setSummary(ringtone.getTitle(getContext()));*/
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Vortex.KEY_AUTOCLEAN_INTERVAL)) {
            setIntervalPreference(sharedPreferences, Vortex.KEY_AUTOCLEAN_INTERVAL, "The system will " +
                    "clean up after", "an one");//for auto clean
        } else if (key.equals(Vortex.PREF_REFRESH_INTERVAL)) {
            setIntervalPreference(sharedPreferences, Vortex.PREF_REFRESH_INTERVAL, "App will " +
                    "update in each", "");//for update
        }
    }

    private void setIntervalPreference(SharedPreferences sharedPreferences, String Key, String text, String one) {
        Preference preference = findPreference(Key);
        Log.v("k", sharedPreferences.getString(Key, "def"));
        String interval = Integer.toString(sharedPreferences.getInt(Key, -1));
        String[] v = interval.split(",");
        String prefix = v[0];
        String suffix = Vortex.Values.get(v[1]);
        String summery = text + " " +
                ((Integer.parseInt(prefix) > 1) ? prefix + " " + suffix + "s"
                        : one + " " + suffix) + ".";
        preference.setSummary(summery);
    }

    @Override
    public void onStop() {
        // TODO: 12/8/2016 save settings in webserver here 
        super.onStop();
    }
}
