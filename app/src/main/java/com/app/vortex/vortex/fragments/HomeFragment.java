package com.app.vortex.vortex.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.vortex.vortex.R;
import com.app.vortex.vortex.activities.LoginActivity;
import com.app.vortex.vortex.app.Vortex;
import com.app.vortex.vortex.services.TankInfoService;
import com.app.vortex.vortex.view.WaterTankBar;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;


/**
 * Home fragment
 */
public class HomeFragment extends Fragment {
    GcmNetworkManager gcmNetworkManager;
    Task task;
    BroadcastReceiver broadcastReceiver;
    int device_id = -1;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gcmNetworkManager = GcmNetworkManager.getInstance(getContext());



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        final WaterTankBar waterTankBar = (WaterTankBar) view.findViewById(R.id.water_tank);

        waterTankBar.setProgress(0);



        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getBundleExtra(TankInfoService.SYSTEM_INFO);
                waterTankBar.setProgress(bundle.getInt(TankInfoService.TAG_TANK_LEVEL));
            }
        };

        return view;
    }

    @Override
    public void onStart() {
        task = createGetUpdateTask();
        gcmNetworkManager.schedule(task);

        LocalBroadcastManager.getInstance(getActivity().getApplication().getApplicationContext())
                .registerReceiver(broadcastReceiver, new IntentFilter(TankInfoService.SYSTEM_INFO));

        super.onStart();
    }

    @Override
    public void onStop() {
        //we don't need to show data while the app not active
        gcmNetworkManager.cancelAllTasks(TankInfoService.class);
        LocalBroadcastManager.getInstance(getActivity().getApplication().getApplicationContext())
                .unregisterReceiver(broadcastReceiver);

        super.onStop();
    }

    private Task createGetUpdateTask()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String t = preferences.getString(Vortex.PREF_REFRESH_INTERVAL, "2,s");
        int period = Vortex.valueToSeconds(t);
        int reflexTime = Vortex.ReflexPeriods.get(t);

        Task task = new PeriodicTask.Builder()
                .setService(TankInfoService.class)
                .setPeriod(period)
                .setFlex(reflexTime)
                .setTag(Vortex.TAG_PERIODIC_UPDATE_TASK)
                .setUpdateCurrent(true)
                .build();

        //Log
        Log.v("task-para", "Period = " + period + " ,Reflex Time = " + reflexTime);

        return task;
    }
}
