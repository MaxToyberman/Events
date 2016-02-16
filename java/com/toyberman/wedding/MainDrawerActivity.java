package com.toyberman.wedding;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.toyberman.wedding.Entities.Contact;
import com.toyberman.wedding.Entities.Pair;
import com.toyberman.wedding.Fragments.EventsFragment;
import com.toyberman.wedding.Fragments.NewEventFragment;
import com.toyberman.wedding.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainDrawerActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FrameLayout mContentFrame;
    private FragmentManager mFragmentManager;
    private String user = null;
    private SharedPreferences pref;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        pref = getSharedPreferences("Details", Context.MODE_PRIVATE);

        user = loadUser();

        //if user was not logged in before
        if (user.equals("0")) {


            Intent login_intent = new Intent(this, LoginActivity.class);
            startActivity(login_intent);
            return;

        }

       // updateAttendance(user);

        if (savedInstanceState == null) {
            mFragmentManager = getSupportFragmentManager();
            mFragmentManager.beginTransaction().replace(R.id.containerView, new NewEventFragment()).commit();
        }
        setUpToolBar();
        setUpNavDrawer();
        setNavigationItemSelectedListener();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
                if (!sentToken)
                    Toast.makeText(getApplicationContext(),getString(R.string.token_error_message),Toast.LENGTH_LONG).show();

            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    private String loadUser() {
        String data = pref.getString("uid", "0");
        return data;
    }

    private void setNavigationItemSelectedListener() {

        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        mContentFrame = (FrameLayout) findViewById(R.id.containerView);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                int id = menuItem.getItemId();
                switch (id) {

                    case R.id.drawer_new:
                        mFragmentManager.beginTransaction().replace(R.id.containerView, new NewEventFragment()).commit();
                        return true;
                    case R.id.drawer_Events:
                        mFragmentManager.beginTransaction().replace(R.id.containerView, new EventsFragment()).commit();
                        return true;
                }
                return true;
            }
        });
    }

    private void setUpToolBar() {

        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private void setUpNavDrawer() {

        if (mToolbar != null) {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                mToolbar.setNavigationIcon(R.mipmap.ic_menu_black_24dp);
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            mDrawerLayout.openDrawer(GravityCompat.START);
                        }
                    }
                });

            }
        }


    }


}
