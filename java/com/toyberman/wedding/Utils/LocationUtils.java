package com.toyberman.wedding.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Maxim Toyberman on 10/16/15.
 */
public class LocationUtils {

    public static LatLng getLatitudeLongtitude(Context ctx,final String location) {

        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
        List<Address> addresses = null;

        double lat=0,lng=0;
        LatLng latLng=null;
        try {
            addresses = geocoder.getFromLocationName(location, 1);
            lat = addresses.get(0).getLatitude();
            lng = addresses.get(0).getLongitude();
            latLng=new LatLng(lat,lng);

        } catch (IOException e) {
            Log.d("Location", e.getMessage());
        }

        return latLng;

    }

    public static String getAddress(Context ctx,LatLng location) throws IOException{
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());

        List<Address> fromLocation = geocoder.getFromLocation(location.latitude, location.longitude, 1);
        Address address = fromLocation.get(0);
        String street = address.getAddressLine(0);
        String city = address.getAddressLine(1);
        String country = address.getAddressLine(2);

        return street + "," + city + "," + country;
    }

    public static String getCountryFromTelephony(Context ctx){

        //get telephone country from the network
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(ctx.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();

        return new Locale("", countryCodeValue).getDisplayCountry();

    }

}
