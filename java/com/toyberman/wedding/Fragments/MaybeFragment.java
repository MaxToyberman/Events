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

public class MaybeFragment extends Fragment {


    private List<Boolean> status;
    private RecyclerView rv_maybe;
    private LinearLayoutManager mLayoutManager;
    public  static rvAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_maybe, container, false);
        rv_maybe = (RecyclerView) v.findViewById(R.id.maybe_recycler_view);


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        rv_maybe.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new rvAdapter(MainActivity.maybe, getContext());
        rv_maybe.setAdapter(mAdapter);



        return v;
    }



}