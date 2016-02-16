package com.toyberman.wedding;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.toyberman.wedding.Entities.Pair;
import com.toyberman.wedding.Utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Maxim Toyberman on 28/11/2015.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private SharedPreferences pref;


    public RegistrationIntentService() {
        super(TAG);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        pref = getSharedPreferences("Details", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {

            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM Registration Token: " + token);
            String response = sendRegistrationToServer(token);
//todo check the response
            sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());

            sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }

    private String sendRegistrationToServer(String token) {

        String uid = loadUid();
        String email = loadEmail();

        ArrayList<Pair> params = new ArrayList<>();
        params.add(new Pair("token", token));
        params.add(new Pair("uid", uid));
        params.add(new Pair("email", email));

        return NetworkUtils.postData(Constants.postToken, params);

    }

    private String loadUid() {
        String data = pref.getString("uid", "0");
        return data;
    }

    private String loadEmail() {
        String data = pref.getString("email", "0");
        return data;
    }
}
