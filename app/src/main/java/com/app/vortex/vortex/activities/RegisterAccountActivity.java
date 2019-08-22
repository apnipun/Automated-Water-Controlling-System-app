package com.app.vortex.vortex.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.vortex.vortex.R;
import com.app.vortex.vortex.app.Vortex;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterAccountActivity extends AppCompatActivity {
    private static final String TERMS_OF_CONDITIONS_PAGE = Vortex.HOST_ADDRESS + "eula.php";
    public static final String PAGE_REGISER = Vortex.HOST_ADDRESS + "auth/register_client.php";

    EditText mTextEmail;
    EditText mTextPassword;
    EditText mTextPasswordVerify;
    EditText mTextDeviceID;
    CheckBox mCheckBoxAgreeToTerms;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        mTextEmail = (EditText) findViewById(R.id.reg_email);
        mTextPassword = (EditText) findViewById(R.id.reg_password);
        mTextDeviceID = (EditText) findViewById(R.id.reg_device_id);
        mTextPasswordVerify = (EditText) findViewById(R.id.reg_validate_password);
        mCheckBoxAgreeToTerms = (CheckBox) findViewById(R.id.checkBoxAgreement);

        final Button buttonRegister = (Button) findViewById(R.id.button_register);
        final TextView textBackToLogin = (TextView) findViewById(R.id.textGoToLogin);
        final TextView textTermsAndConditions = (TextView) findViewById(R.id.textTermsOfAgreements);

        setClearErrorsListeners();

        textTermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_OF_CONDITIONS_PAGE));
                startActivity(intent);
            }
        });

        textBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterAccountActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setClearErrorsListeners() {
        mTextDeviceID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextDeviceID.setError(null);
            }
        });

        mTextPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextPassword.setError(null);
            }
        });

        mTextPasswordVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextPasswordVerify.setError(null);
            }
        });

        mTextEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextEmail.setError(null);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        mTextEmail.setError(null);
        mTextPasswordVerify.setError(null);
        mTextPassword.setError(null);
        mTextDeviceID.setError(null);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    private void attemptRegister() {
        boolean okEmail = isValidEmail();
        if (!okEmail)
            return;

        boolean okPassword = isValidPassword();
        if (!okPassword)
            return;

        boolean okDeviceID = isValidDeviceID();
        if (!okDeviceID)
            return;

        boolean all = okEmail && okDeviceID && okPassword;
        boolean acceptedAgreements = mCheckBoxAgreeToTerms.isChecked();

        if (all) {
            if (!acceptedAgreements) {
                //we do not have selected terms and agreements
                Toast.makeText(getApplicationContext(), getString(R.string.error_accept_agreements)
                        , Toast.LENGTH_LONG)
                        .show();
            } else {
                new RegisterUser().execute(PAGE_REGISER);
            }
        }
    }

    private boolean isValidEmail() {
        String email = mTextEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mTextEmail.setError(getString(R.string.error_field_required));
            mTextEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mTextEmail.setError(getString(R.string.error_invalid_email));
            mTextEmail.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidPassword() {
        String p1 = mTextPassword.getText().toString();
        String p2 = mTextPasswordVerify.getText().toString();

        if (TextUtils.isEmpty(p1)) {
            mTextPassword.setError(getString(R.string.error_field_required));
            mTextPassword.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(p2)) {
            mTextPasswordVerify.setError(getString(R.string.error_field_required));
            mTextPasswordVerify.requestFocus();
            return false;
        } else if (!p1.equals(p2)) {
            mTextPasswordVerify.setError(getString(R.string.error_password_is_not_matching));
            mTextPasswordVerify.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidDeviceID() {
        if (TextUtils.isEmpty(mTextDeviceID.getText().toString())) {
            mTextDeviceID.setError(getString(R.string.error_field_required));
            mTextDeviceID.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("RegisterAccount Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private enum RegistrationStatus {
        Success, UnkonwnError, FirebaseError,
    }

    private class RegisterUser extends AsyncTask<String, Void, RegistrationStatus> {
        private final OkHttpClient client = new OkHttpClient();
        private RequestBody formRequestBody;
        private ResponseResult responseResult;
        private boolean firebase_ok = true;
        private ProgressDialog progressDialog;

        public RegisterUser() {
        }

        @Override
        protected RegistrationStatus doInBackground(String... params) {
            if (!firebase_ok)
                return RegistrationStatus.FirebaseError;

            String url = params[0];
            Request request = new Request.Builder()
                    .url(url)
                    .post(formRequestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                responseResult = parseDataMessage(response.body().string());
            } catch (Exception e) {
                return RegistrationStatus.UnkonwnError;
            }

            return RegistrationStatus.Success;
        }

        @Override
        protected void onPostExecute(RegistrationStatus registrationStatus) {
            switch (registrationStatus) {
                case FirebaseError:
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Firebase Service is not running!."
                            , Toast.LENGTH_LONG).show();
                    break;

                case UnkonwnError:
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Unknown error occured", Toast.LENGTH_LONG).show();
                    break;

                case Success:
                    //we have successfully done a query
                    ProcessSuccess();
                    break;
            }
        }

        private void ProcessSuccess() {
            if (responseResult == null) {
                Toast.makeText(getApplicationContext(), "Unknown error !.", Toast.LENGTH_LONG).show();
            } else {
                if (responseResult.getSuccess() == 1) {
                    //we have registerd, go to home
                    GoToHome();
                } else {
                    if (responseResult.getError_msg() != null || !responseResult.getError_msg().isEmpty()) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), responseResult.getError_msg(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        private void GoToHome() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPreferences.edit()
                    .putInt("user_id", responseResult.getUser_id())
                    .putInt("device_id", responseResult.getDevice_id())
                    .apply();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            progressDialog.dismiss();
            startActivity(intent);
            finish();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String firebase_token = "23243jrewknfkfgnjdsknsdkjvnskvcsdvnjfdgvohdnfbvfdbnvkhfdnhvgbdofhndbv";

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = ProgressDialog.show(RegisterAccountActivity.this, "Registering", "Registering your account...", true);
                }
            });


            if (firebase_token == null || firebase_token.isEmpty()) {
                firebase_ok = false;
                progressDialog.dismiss();
                return;
            }

            formRequestBody = new FormBody.Builder()
                    .add("email", mTextEmail.getText().toString())
                    .add("password", mTextPassword.getText().toString())
                    .add("device_id", mTextDeviceID.getText().toString())
                    .add("firebase_token", firebase_token)
                    .build();

        }

        private ResponseResult parseDataMessage(String st) throws JSONException {
            ResponseResult result = new ResponseResult();
            JSONObject jsonObject = new JSONObject(st);

            if (jsonObject.has("error_msg"))
                result.setError_msg(jsonObject.getString("error_msg"));

            if (jsonObject.has("success"))
                result.setSuccess(jsonObject.getInt("success"));

            if (jsonObject.has("user_id"))
                result.setUser_id(jsonObject.getInt("user_id"));

            if (jsonObject.has("device_id"))
                result.setDevice_id(jsonObject.getInt("device_id"));

            return result;
        }

    }

    private class ResponseResult {
        int user_id = -1;
        int success = 0;
        String error_msg = null;
        int device_id = -1;

        public int getDevice_id() {
            return device_id;
        }

        public void setDevice_id(int device_id) {
            this.device_id = device_id;
        }

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        public String getError_msg() {
            return error_msg;
        }

        public void setError_msg(String error_msg) {
            this.error_msg = error_msg;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }
    }

}
