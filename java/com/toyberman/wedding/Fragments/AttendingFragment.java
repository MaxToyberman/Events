
package com.toyberman.wedding.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toyberman.wedding.Entities.Contact;
import com.toyberman.wedding.MainActivity;
import com.toyberman.wedding.R;
import com.toyberman.wedding.Adapters.rvAdapter;

import java.util.ArrayList;
import java.util.List;


public class AttendingFragment extends Fragment {

    private RecyclerView attending;
    private LinearLayoutManager mLayoutManager;

    public static rvAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_attending, container, false);

        attending = (RecyclerView) view.findViewById(R.id.attending_recycler_view);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        attending.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new rvAdapter(MainActivity.attending,getContext());
        attending.setAdapter(mAdapter);


        return  view;
    }







}