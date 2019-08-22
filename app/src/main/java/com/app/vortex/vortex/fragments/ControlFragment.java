package com.app.vortex.vortex.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.app.vortex.vortex.R;
import com.app.vortex.vortex.app.Vortex;
import com.app.vortex.vortex.services.TankInfoService;
import com.app.vortex.vortex.view.WaterTankBar;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Control fragment
 */
public class ControlFragment extends Fragment {
    GcmNetworkManager gcmNetworkManager;
    Task task;
    BroadcastReceiver broadcastReceiver;

    int tank_level = 0;
    int well_status = 0;
    int motor_status = 0;

    int device_id = 0;
    final static String BASE_ADDRESS = Vortex.HOST_ADDRESS + "maintaince/control_device.php?device_id=";

    public ControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        final Switch switchMotor = (Switch) view.findViewById(R.id.switch_motor);
        final Switch switchManualClean = (Switch) view.findViewById(R.id.switch_manual_clean);
        //final Switch switchTapLine = (Switch) view.findViewById(R.id.switch_tap);
        final WaterTankBar waterTankBar = (WaterTankBar) view.findViewById(R.id.water_tank_control);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        device_id = sharedPreferences.getInt(Vortex.DEVICE_ID, 0);


        switchMotor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String address = BASE_ADDRESS + device_id + "&motor_on=";

                if(isChecked){
                    address += 1;
                    new ControlDevice().execute(address);
                }else{
                    address += 0;
                    new ControlDevice().execute(address);
                }
                // TODO: 12/8/2016 call methods 
            }
        });

        switchManualClean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String address = BASE_ADDRESS + device_id + "&clean=";

                if(tank_level <= 20 && isChecked){
                    address += 1;
                    new ControlDevice().execute(address);
                    return;
                }else if(tank_level > 20){
                    Toast.makeText(getContext(), "Please wait until tank level is below 20%", Toast.LENGTH_LONG).show();
                    switchManualClean.setChecked(false);
                    return;
                }else if(!isChecked){
                    address += 0;
                    new ControlDevice().execute(address);
                }



                // TODO: 12/8/2016 call methods
            }
        });



        TankInfoService service = new TankInfoService();
        service.ManualRefresh(device_id);


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getBundleExtra(TankInfoService.SYSTEM_INFO);
                tank_level = bundle.getInt(TankInfoService.TAG_TANK_LEVEL);
                motor_status = bundle.getInt(TankInfoService.TAG_MORTOR_STATUS);
                well_status = bundle.getInt(TankInfoService.TAG_WELL_STATS);

                //if do not want remove //
                //switchMotor.setChecked(getBool(motor_status));
                //switchManualClean.setChecked(getBool());

                waterTankBar.setProgress(tank_level);
            }
        };

        waterTankBar.setProgress(tank_level);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gcmNetworkManager = GcmNetworkManager.getInstance(getContext());


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
        Log.v("task-para2", "Period = " + period + " ,Reflex Time = " + reflexTime);

        return task;
    }

    private boolean getBool(int i){
        return (i == 1 ? true : false);
    }

    //control motor
    class ControlDevice extends AsyncTask<String, Void, Boolean>{


        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Log.v("adress", params[0]);
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.connect();
                Log.v("conn-w", connection.getResponseMessage());
                InputStream inputStream = connection.getInputStream();
                boolean r = parseMessage(inputStream);
                connection.disconnect();
                return r;
            } catch (Exception e) {
                Log.v("err-msg", e.getMessage());
                return false;
            }
        }

        boolean parseMessage(InputStream in) throws IOException{
            JsonReader reader = new JsonReader(new InputStreamReader(in, "Utf-8"));

            reader.beginObject();
            while (reader.hasNext()){
                String name = reader.nextName();

                if(name == "success"){
                    int i = reader.nextInt();
                    Log.v("success", Integer.toString(i));
                    return getBool(i);
                }else {
                    reader.skipValue();
                    return false;
                }
            }
            reader.endObject();

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }

        private boolean getBool(int i){
            return (i == 1 ? true : false);
        }
    }

}
