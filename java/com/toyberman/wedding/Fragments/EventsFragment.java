package com.toyberman.wedding.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;
import android.widget.CheckBox;
import android.widget.TextView;
import com.toyberman.wedding.Constants;
import com.toyberman.wedding.Entities.Event;
import com.toyberman.wedding.Entities.Pair;
import com.toyberman.wedding.MainActivity;
import com.toyberman.wedding.R;
import com.toyberman.wedding.Utils.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toyberman Maxim on 14-Aug-15.
 */
public class EventsFragment extends Fragment {


    private RecyclerView rv_events;
    private SharedPreferences pref;

    public EventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_events, container, false);
        rv_events = (RecyclerView) v.findViewById(R.id.rv_events);

        pref = getActivity().getSharedPreferences("Details", Context.MODE_PRIVATE);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_events.setLayoutManager(layoutManager);

        updateDataFromServer();

        return v;
    }

    private String loadUid() {
        String data = pref.getString("uid", "0");
        return data;
    }

    private void updateDataFromServer() {
        final List<Event> events = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                String uid = loadUid();
                ArrayList<Pair> params = new ArrayList<>();
                params.add(new Pair("uid", uid));

                String response = NetworkUtils.postData(Constants.eventsForUser, params);

                try {
                    JSONArray eventsJson = new JSONObject(response).getJSONArray("events");

                    for (int i = 0; i < eventsJson.length(); i++) {
                        String wid = eventsJson.getJSONObject(i).getString("wid");
                        String title = eventsJson.getJSONObject(i).getString("title");
                        events.add(new Event(title, wid));
                    }

                } catch (JSONException e) {
                    Log.d("JSONException", e.getMessage());

                }
            }
        });

        thread.start();
        try {
            thread.join();
            rv_events.setAdapter(new MyAdapter(events));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.CustomViewHolder> implements View.OnClickListener {


        private List<Event> events;

        @Override
        public void onClick(View v) {
            int itemPosition = rv_events.getChildAdapterPosition(v);
            Log.d("onclickrv",""+itemPosition);
            Intent main_intent = new Intent(getContext(), MainActivity.class);
            main_intent.putExtra("wedding_id", events.get(itemPosition).getEventiD());
            startActivity(main_intent);

        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTextViewEvent;
            public CheckBox mCheckBox;
            public TextView mTextViewNumber;

            public CustomViewHolder(View view) {
                super(view);

                this.mTextViewEvent = (TextView) view.findViewById(R.id.tv_name);
                this.mCheckBox = (CheckBox) view.findViewById(R.id.cb_check_box);
                this.mTextViewNumber = (TextView) view.findViewById(R.id.tv_number);

                mCheckBox.setVisibility(View.GONE);
                mTextViewNumber.setVisibility(View.GONE);
            }


        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(List<Event> events) {

            this.events = events;

        }

        // Create new views (invoked by the layout manager)
        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
            // create a new view
            View v = LayoutInflater.from(getActivity())
                    .inflate(R.layout.contacts_list_item, parent, false);
            // set the view's size, margins, paddings and layout parameters

            v.setOnClickListener(this);

            CustomViewHolder vh = new CustomViewHolder(v);

            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            String eventName = events.get(position).getEventName();

            holder.mTextViewEvent.setText(eventName);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return events.size();
        }


    }
}
