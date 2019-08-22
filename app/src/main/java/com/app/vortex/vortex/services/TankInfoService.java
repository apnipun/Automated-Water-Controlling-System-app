package com.app.vortex.vortex.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.JsonReader;
import android.util.Log;

import com.app.vortex.vortex.app.Vortex;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;

public class TankInfoService extends GcmTaskService {
    LocalBroadcastManager localBroadcastManager;
    Bundle bundle;

    public static final String SYSTEM_INFO = "com.vortex.vortex.sys_info";//main intent filter
    public static final String HOST_FILE = "data.php";//host file to acquire data

    public static final String TAG_TANK_LEVEL = "tank_level";
    public static final String TAG_MORTOR_STATUS = "motor_status";
    public static final String TAG_WELL_STATS = "well_stats";
    public static final String SYSTEM_NOT_SYNCED = "com.vortex.vortex.sys_not_synced";
    public String REQUEST = "maintaince/client_feed.php?device_id=";

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        REQUEST += sharedPreferences.getInt(Vortex.DEVICE_ID, -1);
    }

    public TankInfoService()
    {

    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        //Log.v("task-pr", "i run");

        switch (taskParams.getTag())
        {
            case Vortex.TAG_ONEOFF_UPDATE_TASK:
                //do one off task here
                return GcmNetworkManager.RESULT_SUCCESS;

            case Vortex.TAG_PERIODIC_UPDATE_TASK:
                new GetDateFromServer().execute(Vortex.HOST_ADDRESS + REQUEST);
                return GcmNetworkManager.RESULT_SUCCESS;

            default:
                return GcmNetworkManager.RESULT_FAILURE;
        }
    }

    public void ManualRefresh(int device_id){
        REQUEST += device_id;

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        new GetDateFromServer().execute(Vortex.HOST_ADDRESS + REQUEST);
        Log.v("add", Vortex.HOST_ADDRESS + REQUEST);
    }

    class GetDateFromServer extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.connect();
                Log.v("conn-w", connection.getResponseMessage());
                InputStream inputStream = connection.getInputStream();
                bundle = readDataMessage(inputStream);
                connection.disconnect();
                return true;
            } catch (Exception e) {
                Log.v("err-msg", e.getMessage());
                return false;
            }
        }

        private Bundle readDataMessage(InputStream in) throws IOException
        {
            Bundle bundle = new Bundle();
            JsonReader reader = new JsonReader(new InputStreamReader(in, "Utf-8"));

            reader.beginObject();
            while (reader.hasNext())
            {
                String tag = reader.nextName();
                //Log.v("tag",tag);
                switch (tag){
                    case TAG_TANK_LEVEL:
                        bundle.putInt(TAG_TANK_LEVEL, Integer.parseInt(reader.nextString()));
                        break;

                    case TAG_MORTOR_STATUS:
                        bundle.putInt(TAG_MORTOR_STATUS, Integer.parseInt(reader.nextString()));
                        break;

                    case TAG_WELL_STATS:
                        bundle.putInt(TAG_WELL_STATS, Integer.parseInt(reader.nextString()));
                        break;

                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();

            return bundle;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success)
            {
                Intent intent = new Intent(SYSTEM_INFO);
                intent.putExtra(SYSTEM_INFO, bundle);
                localBroadcastManager.sendBroadcast(intent);
            }else{
                Intent intent = new Intent(SYSTEM_INFO);
                Bundle bundle = new Bundle();
                bundle.putBoolean(SYSTEM_NOT_SYNCED, true);
                intent.putExtra(SYSTEM_INFO, bundle);
                localBroadcastManager.sendBroadcast(intent);
            }
        }
    }


}
