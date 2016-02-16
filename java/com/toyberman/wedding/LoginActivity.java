package com.toyberman.wedding;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.toyberman.wedding.Entities.Pair;
import com.toyberman.wedding.Utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Toyberman Maxim on 14-Aug-15.
 */
public class LoginActivity extends AppCompatActivity {


    // UI references.
    @Bind(R.id.btn_signup)
    Button btn_signup;
    @Bind(R.id.btn_login)
    Button btn_login;
    @Bind(R.id.email)
    AutoCompleteTextView mEmailView;
    @Bind(R.id.password)
    EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mSignIn;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        pref = getSharedPreferences("Details", Context.MODE_PRIVATE);

    }

    @OnClick(R.id.btn_login)
    public void btnLogin(View v) {


        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {

                String jsonResult = "";

                ArrayList<Pair> parameters = new ArrayList<>();
                try {
                    String res=SHA256(params[1]);
                    Log.d("result",res);
                    parameters.add(new Pair("email", params[0]));
                    parameters.add(new Pair("psw", SHA256(params[1])));

                    jsonResult = NetworkUtils.postData(Constants.loginURL, parameters);

                } catch (NoSuchAlgorithmException e) {
                    Log.d("EVENTS", e.getMessage());
                }
                return jsonResult;
            }

            @Override
            protected void onPostExecute(String response) {

                JSONObject result = null;
                try {
                    result = new JSONObject(response);

                    String status = result.getString("status");

                    if (status.equals("success")) {

                        saveEmail(mEmailView.getText().toString());
                        saveUserPassword(SHA256(mPasswordView.getText().toString()));

                        Toast.makeText(getApplicationContext(), "Logged in!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(), MainDrawerActivity.class);
                        saveUid(result.getString("uid"));
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Login Parameters!", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }


            }
        }.execute(mEmailView.getText().toString(), mPasswordView.getText().toString());

    }

    @OnClick(R.id.btn_signup)
    public void btnSignUp(View v) {


        if (!isEmailValid(mEmailView.getText().toString())) {

            mEmailView.setError("Email is not valid");
            return;
        }

        if (!isPasswordValid(mPasswordView.getText().toString())) {

            mPasswordView.setError("Password must have at least one digit,a lower case letter,upper case letter,special character,no whitespace");
            return;

        }

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {

                String jsonResult = "";
                ArrayList<Pair> parameters = new ArrayList<>();
                try {
                    parameters.add(new Pair("email", params[0]));
                    parameters.add(new Pair("psw", SHA256(params[1])));
                    parameters.add(new Pair("dev_id", params[2]));

                    jsonResult = NetworkUtils.postData(Constants.registerURL, parameters);


                } catch (NoSuchAlgorithmException e) {
                    Log.d("EVENTS", e.getMessage());
                }

                return jsonResult;
            }

            @Override
            protected void onPostExecute(String response) {

                JSONObject jsonResultObject = null;
                String result = "";
                try {
                    jsonResultObject = new JSONObject(response);

                    if (jsonResultObject.has("uid")) {
                        result = jsonResultObject.getString("uid");

                        saveUid(result);
                        saveEmail(mEmailView.getText().toString());
                        saveUserPassword(SHA256(mPasswordView.getText().toString()));

                        Toast.makeText(getApplicationContext(), "Logged in!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(), MainDrawerActivity.class);
                        startActivity(intent);
                    } else {
                        result = jsonResultObject.getString("error");
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }.execute(mEmailView.getText().toString(), mPasswordView.getText().toString(), Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

    }

    private void saveEmail(String data) {
        Editor editor = pref.edit();
        editor.putString("email", data);
        editor.commit();
    }

    private void saveUid(String data) {
        Editor editor = pref.edit();
        editor.putString("uid", data);
        editor.commit();
    }

    private void saveUserPassword(String password) {
        Editor editor = pref.edit();
        editor.putString("password", password);
        editor.commit();
    }

    private boolean isEmailValid(String email) {

        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);

        return m.matches();
    }

    private boolean isPasswordValid(String password) {

        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{5,10}";

        return password.matches(pattern);
    }


    public String SHA256(String text) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(text.getBytes());
        byte[] digest = md.digest();

        return bytesToHexString(digest);

    }

    private  String bytesToHexString(byte[] bytes) {


        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        Log.d("check",sb.toString());
        return sb.toString();
    }



}

