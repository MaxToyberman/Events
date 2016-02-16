package com.toyberman.wedding.Fragments;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;
import com.toyberman.wedding.Constants;
import com.toyberman.wedding.Entities.Contact;
import com.toyberman.wedding.Entities.Pair;
import com.toyberman.wedding.MainActivity;
import com.toyberman.wedding.R;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.toyberman.wedding.Utils.NetworkUtils;
import com.toyberman.wedding.Adapters.rvAdapter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Toyberman Maxim on 16-Aug-15.
 */
public class InviteFragment extends Fragment implements SearchView.OnQueryTextListener {

    private RecyclerView rv_invite;
    private Button btn_done;
    private List<String> numbers;
    private AlertDialog ratingDialog;
    private String message;
    private ArrayList<Contact> contacts;
    private ArrayList<Contact> temp_contacts;
    private LinearLayoutManager mLayoutManager;

    private rvAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AttendingFragment","1");
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_invite, container, false);

        numbers = new ArrayList<>();
        contacts = new ArrayList<>();
        //find done button
        btn_done = (Button) view.findViewById(R.id.btn_done);
        //find recyclerview
        rv_invite = (RecyclerView) view.findViewById(R.id.rv_invite);
        //get contacts list
        setContacts(numbers);
        //sorting the contact list
        Collections.sort(contacts);
        temp_contacts=new ArrayList<>(contacts);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        //mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_invite.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new rvAdapter(contacts, getContext());
        rv_invite.setAdapter(mAdapter);


        return view;
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_add:

                return  true;

            case R.id.action_search:
                return false;

        }
        return false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initEvents();

    }


    private void setContacts(List<String> numbers) {


        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            //contact phone number
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String toDisplay = toNumberFormat(phoneNumber);
            //checking for duplicates
            if (numbers.contains(toDisplay))
                continue;

            //contact name
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            numbers.add(toDisplay);

            contacts.add(new Contact(name, phoneNumber));

        }
        phones.close();

    }

    private String toNumberFormat(String phone_number) {

        String new_phone = "";
        for (int i = 0; i < phone_number.length(); i++) {
            char c = phone_number.charAt(i);
            if (Character.isDigit(c))
                new_phone += c;
        }

        return new_phone;
    }

    private void displayRatingDialog(List<Contact> to_numbers) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View messageView = getActivity().getLayoutInflater().inflate(R.layout.message, null, false);
        initDialogEvents(messageView, to_numbers);
        builder.setView(messageView);
        builder.setCancelable(false);
        ratingDialog = builder.show();
        //TODO DISSMISS ON-BACK BUTTON
    }

    private void initDialogEvents(final View messageView, final List<Contact> to_numbers) {

        final EditText et_message = (EditText) messageView.findViewById(R.id.et_message);
        final Button btn_send = (Button) messageView.findViewById(R.id.btn_send);

        message = getString(R.string.default_message);
        et_message.setText(message);

        btn_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                message = et_message.getText().toString();
                ratingDialog.dismiss();
                try {
                    sendSMS(to_numbers, message);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendSMS(List<Contact> to_contacts, final String message) throws UnsupportedEncodingException, MalformedURLException {
        final SmsManager manager = SmsManager.getDefault();



        for (final Contact contact : to_contacts) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    ArrayList<Pair> post_parameters = new ArrayList<Pair>(3);
                    post_parameters.add(new Pair("number", contact.getPhone_number()));
                    post_parameters.add(new Pair("name", contact.getName()));
                    post_parameters.add(new Pair("weddingId", MainActivity.wedding_id));

                    String short_url_json = NetworkUtils.postData(Constants.sendGuestInvitationURL, post_parameters);

                    try {

                        JSONObject short_url = new JSONObject(short_url_json);
                        String url = short_url.getString("shortUrl");
                        String lineSep = System.getProperty("line.separator");
                        ArrayList<String> parts = manager.divideMessage(message + lineSep + url);
                        manager.sendMultipartTextMessage(contact.getPhone_number(), null, parts, null, null);
                        //manager.sendTextMessage(contact.getPhone_number(), null,message + lineSep +url, null, null);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    private void initEvents() {

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Contact> to_numbers = new ArrayList<Contact>();
                for (Contact contact : contacts) {
                    if (contact.isSelected()) {
                        to_numbers.add(contact);
                    }
                }

                displayRatingDialog(to_numbers);

            }
        });


    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        final List<Contact> filteredModelList = filter(temp_contacts, query);

        mAdapter.animateTo(filteredModelList);
        rv_invite.scrollToPosition(0);

        return true;
    }


    private List<Contact> filter(List<Contact> contacts, String query) {
        query = query.toLowerCase();

        final List<Contact> filteredModelList = new ArrayList<>();
        for (Contact contact : contacts) {
            final String name = contact.getName().toLowerCase();

            final String number = contact.getPhone_number();
            if (name.contains(query)) {
                filteredModelList.add(new Contact(name,number));
            }
        }

        return filteredModelList;
    }
}