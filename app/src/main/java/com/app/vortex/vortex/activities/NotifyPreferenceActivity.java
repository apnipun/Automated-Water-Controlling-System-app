package com.app.vortex.vortex.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app.vortex.vortex.R;
import com.app.vortex.vortex.app.Vortex;

public class NotifyPreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_preference);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NotifyPreferenceFragment notifyPreferenceFragment = new NotifyPreferenceFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_notify_preference, notifyPreferenceFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    public static class NotifyPreferenceFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        public NotifyPreferenceFragment() {

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.notify_preference);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            setNotifyMeSummery(sharedPreferences);
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
            if (key.equals(Vortex.KEY_NOTIFY_ME)) {
                setNotifyMeSummery(sharedPreferences);
            }
        }

        private void setNotifyMeSummery(SharedPreferences sharedPreferences) {
            Preference preference = findPreference(Vortex.KEY_NOTIFY_ME);
            int v = Integer.parseInt(sharedPreferences.getString(Vortex.KEY_NOTIFY_ME, ""));
            String summery;
            if (v > 0) {
                summery = "App will notify you on tank level below " + v + "%%";
            } else {
                summery = "App will not notify you";
            }
            preference.setSummary(summery);
        }
    }

}
