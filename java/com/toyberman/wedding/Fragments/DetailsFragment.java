package com.toyberman.wedding.Fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.toyberman.wedding.Adapters.PlaceAutocompleteAdapter;
import com.toyberman.wedding.R;
import com.toyberman.wedding.Utils.LocationUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public interface OnDetailsTabFilledListener {
        void dateFilled(String date);

        void timeFilled(String time);

        void placeFilled(String place);
    }


    OnDetailsTabFilledListener mCallback;
    private LocationManager locationManager;
    private boolean mapClicked;
    private LatLng countryLatLng;
    private String country;
    private static Location mLastLocation;
    private static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;

    @Bind(R.id.et_date)
    EditText et_date;
    @Bind(R.id.et_time)
    EditText et_time;
    @Bind(R.id.autocomplete_places)
    AutoCompleteTextView autocomplete_places;


    public DetailsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //device country
        country = LocationUtils.getCountryFromTelephony(getContext());
        countryLatLng = LocationUtils.getLatitudeLongtitude(getContext(), country);
        //attaching newEventFragment
        Fragment fragment = getParentFragment();
        onAttachFragment(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mapClicked = false;
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);
        //get reference to location manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        buildGoogleApiClient();
        //adapter instance for auto complete
        final ArrayAdapter<String> adapter = new PlaceAutocompleteAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, mGoogleApiClient);
        autocomplete_places.setAdapter(adapter);

        registerListeners();

        return view;
    }

    private void registerListeners() {

        //set onitemclick listener to auto complete
        autocomplete_places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String location = (String) parent.getItemAtPosition(position);

                mCallback.placeFilled(location);

            }
        });

        if (mMap == null) {
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mapClicked = true;
                    mMap.clear();
                    MarkerOptions options = new MarkerOptions();
                    mMap.addMarker(options.position(latLng));

                    try {
                        String address = LocationUtils.getAddress(getContext(), latLng);

                        autocomplete_places.setText(address);
                    } catch (Exception e) {

                    }
                }
            });

            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(cameraPosition.target)
                                    .tilt(0)
                                    .zoom(cameraPosition.zoom)
                                    .build()));
                }
            });
        }

    }


    @OnTextChanged(value = R.id.et_date, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onDateChanged(CharSequence text) {
        mCallback.dateFilled(text.toString());
    }

    @OnTextChanged(value = R.id.et_time, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onTimeChanged(CharSequence text) {
        mCallback.timeFilled(text.toString());
    }

    @OnClick(R.id.et_date)
    public void showCalendar() {
        //Date picker
        DialogFragment picker = new DatePickerFragment();
        picker.show(getActivity().getFragmentManager(), "datePicker");
    }

    @OnClick(R.id.et_time)
    public void showTime() {
        //Time picker
        DialogFragment picker = new TimePickerFragment();
        picker.show(getActivity().getFragmentManager(), "timePicker");

    }


    public void setTextError(String name, String s) {

        if (name.equals("date")) {
            et_date.setError(s);
        }
        if (name.equals("time")) {
            et_time.setError(s);
        }
        if (name.equals("address")) {
            autocomplete_places.setError(s);
        }

    }

    public void onAttachFragment(Fragment fragment) {
        try {
            mCallback = (OnDetailsTabFilledListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(fragment.toString() + " must implement OnPlayerSelectionSetListener");
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

    }


    public void setUpMap(double lat, double lng) {
        mMap.clear();

        LatLng mPos = new LatLng(lat, lng);
        UiSettings settings = mMap.getUiSettings();
        settings.setAllGesturesEnabled(true);
        settings.setMyLocationButtonEnabled(true);
        settings.setMapToolbarEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MarkerOptions options = new MarkerOptions();
        mMap.addMarker(options.position(mPos));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mPos, 15));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

        //if map click don't update map (leave last location)
        if (mapClicked)
            return;

        //trying to get the last known location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        MarkerOptions options = new MarkerOptions();

        if (mLastLocation != null) {
            setUpMap(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            options.position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).draggable(true);
        } else {

            try {
                setUpMap(countryLatLng.latitude, countryLatLng.longitude);
                options.position(new LatLng(countryLatLng.latitude, countryLatLng.longitude)).draggable(true);
            } catch (Exception e) {
                setUpMap(0, 0);
                options.position(new LatLng(0, 0)).draggable(true);
            }
        }

        mMap.addMarker(options).setDraggable(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("WEDDING", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("WEDDING", "onConnectionFailed");

    }


}