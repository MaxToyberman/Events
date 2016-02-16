package com.toyberman.wedding.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.toyberman.wedding.Adapters.MyFragmentPagerAdapter;
import com.toyberman.wedding.R;
import com.toyberman.wedding.RegistrationIntentService;
import com.toyberman.wedding.UploadFileAsync;
import com.toyberman.wedding.Utils.LocationUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewEventFragment extends Fragment implements PhotoFragment.OnPhotoTabFilledListener, DetailsFragment.OnDetailsTabFilledListener {

    public static Calendar mCalendar;

    public static String android_id;
    private String encodedImage;
    private String uid;
    private DetailsFragment detailsFragment;
    private PhotoFragment photoFragment;
    private SharedPreferences pref;
    private String date;
    private String time;
    private String address;
    private String title;
    private String description;
    @Bind(R.id.fab_save)
    FloatingActionButton fab_save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tabbed_details, container, false);
        ButterKnife.bind(this, view);
        //getting the device unique id
        android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        //get calendar instance
        mCalendar = Calendar.getInstance();
        pref = getActivity().getSharedPreferences("Details", Context.MODE_PRIVATE);
        uid=loadUid();
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        //pager adapter
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getChildFragmentManager());

        photoFragment = new PhotoFragment();
        detailsFragment = new DetailsFragment();
        adapter.addFragment(photoFragment, "Photo");
        adapter.addFragment(detailsFragment, "Details");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);



        return view;

    }
    private String loadUid() {
        String data = pref.getString("uid", "0");
        return data;
    }


    public String convertDateToUTC() {

        //TODO HANDLE hours to be 24 format
        mCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = mCalendar.getTime();
        return time.toString();
    }


    @OnClick(R.id.fab_save)
    public void saveClicked() {
        save();
    }


    private void save() {

        if (checkDetails()) {


            String location = "";

            LatLng coordinates = LocationUtils.getLatitudeLongtitude(getContext(), address);
            location=coordinates.latitude+","+coordinates.longitude;
            String date = convertDateToUTC();

            new UploadFileAsync(title, date, description, location, uid, encodedImage, getActivity()).execute();
        }


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
             notifying child fragments that activity result received
         */
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private boolean checkDetails() {
        /*TODO CHECK FOR IMAGE IS NOT NULL*/
        if (date == null) {
            detailsFragment.setTextError("date", "please choose a date");
            return false;
        }
        if (time == null) {
            detailsFragment.setTextError("time", "please choose a time");
            return false;
        }
        if (address == null) {
            detailsFragment.setTextError("address", "please choose an address");
            return false;
        }
        if (title == null) {
            photoFragment.setTextError("title", "please choose a title");
            return false;
        }
        if (description == null) {
            photoFragment.setTextError("description", "please choose a description");
            return false;
        }
        return true;
    }

    //communiction between fragments and the activity implementing a listener
    @Override
    public void dateFilled(String date) {
        this.date = date;
    }

    @Override
    public void timeFilled(String time) {
        this.time = time;
    }

    @Override
    public void placeFilled(String address) {
        this.address = address;
        System.out.println(address);
        try {
            LatLng coordinates=LocationUtils.getLatitudeLongtitude(getContext(), address);

            detailsFragment.setUpMap(coordinates.latitude, coordinates.longitude);

        } catch (Exception e) {
                Log.d("wedding",e.getMessage());
        }
    }

    @Override
    public void titleFilled(String title) {
        this.title = title;
    }

    @Override
    public void descriptionFilled(String description) {
        this.description = description;
    }

    @Override
    public void imageChosen(String encodedImage) {
        this.encodedImage = encodedImage;
    }
}