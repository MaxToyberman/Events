package com.toyberman.wedding;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.toyberman.wedding.Adapters.MyFragmentPagerAdapter;
import com.toyberman.wedding.Entities.Contact;
import com.toyberman.wedding.Entities.Pair;
import com.toyberman.wedding.Fragments.AttendingFragment;
import com.toyberman.wedding.Fragments.InviteFragment;
import com.toyberman.wedding.Fragments.MaybeFragment;
import com.toyberman.wedding.Fragments.NotAttendingFragment;
import com.toyberman.wedding.Utils.DataBaseHelper;
import com.toyberman.wedding.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Toyberman Maxim on 14-Aug-15.
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SharedPreferences pref;
    //static variables
    public static String wedding_id;
    //array lists of guests
    //public  List<Contact> contacts;
    public static List<Contact> attending;
    public static List<Contact> not_attending;
    public static List<Contact> maybe;


    private Intent mIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("Details", Context.MODE_PRIVATE);


        Intent details_intent = getIntent();
        //get the new wedding id
        wedding_id = details_intent.getStringExtra("wedding_id");

        initLayout();
    }

    private void initLayout() {
        attending = new ArrayList<>();
        not_attending = new ArrayList<>();
        maybe = new ArrayList<>();

        //init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolBar);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabLayout);

        setSupportActionBar(toolbar);
        //fragment pager adapter
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        //adding fragments to the the adapter
        adapter.addFragment(new InviteFragment(), "INVITE");
        adapter.addFragment(new AttendingFragment(), "ATTENDING");
        adapter.addFragment(new NotAttendingFragment(), "NOT ATTENDING");
        adapter.addFragment(new MaybeFragment(), "MAYBE");
        //setting adapter
        viewPager.setAdapter(adapter);
        //setting tab layout properties
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);


        getGuestInfo(wedding_id);



    }
    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter("com.toyberman.MESSAGE_BROADCAST"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");

            Log.d("mMessageReceiver",message);

            try {
                JSONObject json=new JSONObject(message);
                String wid=json.getString("wid");
                if(wid.equals(wedding_id)){
                    JSONArray array=json.getJSONArray("changes");
                    for (int i = 0; i <array.length() ; i++) {
                        String name=array.getJSONObject(i).getString("name");
                        String status=array.getJSONObject(i).getString("status");
                        String phone=array.getJSONObject(i).getString("phone");

                        Contact contact=removeContact(name);
                        if (contact==null){

                            contact=new Contact(name,phone);
                        }
                        if (status.equals("2")){
                            attending.add(contact);

                        }
                        else if (status.equals("3")) {
                            not_attending.add(contact);


                        }//maybe
                        else if (status.equals("4")) {
                            maybe.add(contact);


                        }
                    }

                    if(AttendingFragment.mAdapter!=null)
                        AttendingFragment.mAdapter.notifyDataSetChanged();

                    if(NotAttendingFragment.mAdapter!=null)
                        NotAttendingFragment.mAdapter.notifyDataSetChanged();

                    if(MaybeFragment.mAdapter!=null)
                        MaybeFragment.mAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                Log.d("JSONException",e.getMessage());
            }

            //do other stuff here
        }
    };





    /*
        @Override
        protected void onStop() {
            super.onStop();


            DataBaseHelper db = DataBaseHelper.getInstance(this);
            if(contacts!=null) {
                db.addContacts(contacts,wedding_id);

            }
            db.closeDB();


        }
    */

    public Contact removeContact(String name){

        for (int i = 0; i <attending.size() ; i++) {
            if(attending.get(i).getName().equals(name)) {

                return attending.remove(i);
            }
        }

        for (int i = 0; i <not_attending.size() ; i++) {
            if(not_attending.get(i).getName().equals(name)) {
                return not_attending.remove(i);

            }
        }

        for (int i = 0; i <maybe.size() ; i++) {
            if(maybe.get(i).getName().equals(name)) {
                return maybe.remove(i);

            }
        }

        return  null;

    }
    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }


    private void getGuestInfo(final String wid) {

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected Void doInBackground(Void... params) {

                ArrayList<Pair> pair = new ArrayList<>();
                pair.add(new Pair("wid", wid));
                String json = NetworkUtils.postData(Constants.guestListURL, pair);
                JSONArray guest_list = null;
                try {
                    guest_list = new JSONArray(json);
                    for (int i = 0; i < guest_list.length(); i++) {

                        JSONObject guest = guest_list.getJSONObject(i);
                        String name = guest.getString("name");
                        String phone = guest.getString("phone");
                        String status = guest.getString("status");
                        //guest confirmed
                        if (status.equals("2")) {
                            attending.add(new Contact(name, phone));
                        }//guest declined
                        else if (status.equals("3")) {
                            not_attending.add(new Contact(name, phone));
                        }//maybe
                        else if (status.equals("4")) {
                            maybe.add(new Contact(name, phone));

                        }
                    }

                    pair.clear();



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String uid = loadUid();
                pair.add(new Pair("uid", uid));
                NetworkUtils.postData(Constants.updateAttendance, pair);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {


            }
        }.execute();


    }

    private String loadUid() {
        String data = pref.getString("uid", "0");
        return data;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
