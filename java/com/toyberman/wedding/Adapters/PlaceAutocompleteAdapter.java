
package com.toyberman.wedding.Adapters;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by Toyberman Maxim on 14-Oct-15.
 */
public class PlaceAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {


    private final Location mLastLocation;


    private ArrayList<String> resultList;
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds BOUNDS = null;

    public PlaceAutocompleteAdapter(Context context, int textViewResourceId, GoogleApiClient mGoogleApiClient) {
        super(context, textViewResourceId);
        this.mGoogleApiClient = mGoogleApiClient;


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            BOUNDS = new LatLngBounds(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        } else {
            BOUNDS = new LatLngBounds(new LatLng(31.252973, 34.791462), new LatLng(31.252973, 34.791462));
        }
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private ArrayList<String> autocomplete(String input) {

        PendingResult<AutocompletePredictionBuffer> results =
                Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, input,
                        BOUNDS, null);

        AutocompletePredictionBuffer autocompletePredictions = results.await(60, TimeUnit.SECONDS);
        final Status status = autocompletePredictions.getStatus();
        if (!status.isSuccess()) {
            Toast.makeText(getContext(), "Error: " + status.toString(), Toast.LENGTH_SHORT).show();
            Log.d("autocomplete", "Error getting place predictions: " + status.toString());
            autocompletePredictions.release();
            return null;
        }
        Log.i("autocomplete", "Query completed. Received " + autocompletePredictions.getCount() + " predictions.");

        Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
        ArrayList<String> resultList = new ArrayList<>(autocompletePredictions.getCount());

        while (iterator.hasNext()) {
            AutocompletePrediction prediction = iterator.next();
            resultList.add(prediction.getDescription());
            Log.d("autocomplete", prediction.getDescription());
        }
        autocompletePredictions.release();
        return resultList;
    }

}