package com.app.vortex.vortex.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.vortex.vortex.R;
import com.app.vortex.vortex.app.SettingsHelper;
import com.app.vortex.vortex.app.URLHelper;
import com.app.vortex.vortex.app.Vortex;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    public static final String QUERY_PAGE = Vortex.HOST_ADDRESS + "auth/login_client.php";
    public static final String QUERY_SETTINGS = Vortex.HOST_ADDRESS + "maintaince/maintaince.php";

    private int user_id = -1;
    private int device_id = -1;
    SettingsHelper settingsHelper = null;
    EditText textEmail;
    EditText textPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button loginButton = (Button) findViewById(R.id.button_login);
        final TextView registerPage = (TextView) findViewById(R.id.action_register_page);

        textEmail = (EditText) findViewById(R.id.email);
        textPassword = (EditText) findViewById(R.id.password);

        textEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textEmail.setError(null);
            }
        });

        textPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPassword.setError(null);
            }
        });

        registerPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterAccountActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        textPassword.setError(null);
        textEmail.setError(null);
    }

    private void attemptLogin()
    {
        String email = textEmail.getText().toString();
        String password = textPassword.getText().toString();

        textPassword.setError(null);
        textEmail.setError(null);

        if(isValidEmail(email) && isValidPassword(password)){
            new LoginTask(QUERY_PAGE).execute();
        }
    }

    private boolean isValidEmail(String email)
    {
        if(TextUtils.isEmpty(email)) {
            textEmail.setError(getString(R.string.error_field_required));
            textEmail.requestFocus();
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            textEmail.setError(getString(R.string.error_invalid_email));
            textEmail.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password){
        if(TextUtils.isEmpty(password)){
            textPassword.setError(getString(R.string.error_invalid_password));
            textPassword.requestFocus();
            return false;
        }

        return true;
    }

    public enum LoginResult{
        Success,Unsuccess,UnknownError
    }

    private class LoginTask extends AsyncTask<Void, Void, LoginResult>{
        private ProgressDialog progressDialog;
        private String url;
        private final OkHttpClient httpClient = new OkHttpClient();
        private FormBody formBody;
        private Request request;

        public LoginTask(String url) {
            this.url = url;
        }

        @Override
        protected LoginResult doInBackground(Void... params) {

            try {
                Response response = httpClient.newCall(request).execute();
                LoginResult result = processData(response.body().string());


                return result;
            }catch (Exception e){
                return LoginResult.UnknownError;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = ProgressDialog.show(LoginActivity.this, "Logging in", "Logging into service...", true);
                }
            });

            formBody = new FormBody.Builder()
                    .add("email", textEmail.getText().toString())
                    .add("password", textPassword.getText().toString())
                    .build();

            request = new Request.Builder()
                    .url(this.url)
                    .post(formBody)
                    .build();
        }

        @Override
        protected void onPostExecute(LoginResult loginResult) {
            switch (loginResult){
                case Success:
                    new AquireSettingsTask(httpClient, QUERY_SETTINGS, progressDialog).execute();
                    break;

                case Unsuccess:
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"The email or password is incorrect", Toast.LENGTH_LONG).show();
                    break;

                default:
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Unknown error occured", Toast.LENGTH_LONG).show();
                    break;
            }
        }

        private LoginResult processData(String st) throws JSONException{
            JSONObject jsonObject = new JSONObject(st);
            LoginResult result = LoginResult.UnknownError;

            if(jsonObject.has("error_msg"))
                return LoginResult.UnknownError;

            if(jsonObject.has("success")){
                int s = jsonObject.getInt("success");
                if (s == 1){
                    user_id = jsonObject.getInt("user_id");
                    device_id = jsonObject.getInt("device_id");
                    return LoginResult.Success;
                }else{
                    return LoginResult.Unsuccess;
                }
            }

            return result;
        }

    }

    private class AquireSettingsTask extends AsyncTask<Void, Void, Boolean>{
        private String url;
        private OkHttpClient httpClient;
        private Request request;
        private Response response;
        private ProgressDialog progressDialog;

        public AquireSettingsTask(OkHttpClient httpClient, String url, ProgressDialog progressDialog) {
            this.httpClient = httpClient;
            this.progressDialog = progressDialog;

            this.url = new URLHelper.Builder()
                    .setPath(url)
                    .addQueryParam("user_id", Integer.toString(user_id))
                    .addQueryParam("command", "get_settings")
                    .build()
                    .toString();
        }


        @Override
        protected void onPreExecute() {
            request = new Request.Builder()
                    .url(this.url)
                    .build();
        }

        @Override
        protected void onPostExecute(Boolean success) {


            if(success){
                //ok good to go LOGIN
                Login();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                progressDialog.dismiss();
                startActivity(intent);
                finish();
            }else {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"An unknown error occurred.Try again later", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try{
                response = httpClient.newCall(request).execute();
                String res = response.body().string();
                Log.v("json1", res);
                return parseResult(res);
            }catch (Exception e){
                Log.v("err", e.getMessage());
                return false;
            }
        }

        private boolean parseResult(String res) throws JSONException{
            JSONObject object = new JSONObject(res);

            if(object.has("success")){
                //sorry there is an error, we don't output success for a q
                Log.v("json2", object.toString());
                return false;
            }




            return true;

        }

        public void Login(){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if(settingsHelper != null)
                settingsHelper.saveSettings(editor);

            editor.putInt(Vortex.DEVICE_ID, device_id).putInt(Vortex.USER_ID, user_id).apply();
        }

        int toInt(String s){
            return Integer.parseInt(s);
        }

    }
}
