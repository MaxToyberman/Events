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


public class NotAttendingFragment extends Fragment {

    private RecyclerView rv_not_attending;

    private RecyclerView.LayoutManager mLayoutManager;
    public static  rvAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AttendingFragment", "3");
        View v = inflater.inflate(R.layout.fragment_not_attending, container, false);

        rv_not_attending = (RecyclerView) v.findViewById(R.id.not_attending_recycler_view);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        rv_not_attending.setLayoutManager(mLayoutManager);

        mAdapter = new rvAdapter(MainActivity.not_attending, getContext());
        rv_not_attending.setAdapter(mAdapter);

        return v;
    }




}