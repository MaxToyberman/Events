package com.toyberman.wedding.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import com.toyberman.wedding.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Toyberman Maxim  on 9/7/15.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        //Calendar c = Calendar.getInstance();
        NewEventFragment.mCalendar.set(year, month, day);
        //organizing the format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //get formated date
        String formattedDate = sdf.format(NewEventFragment.mCalendar.getTime());
        //find the et_date
        EditText et_date = (EditText) getActivity().findViewById(R.id.et_date);
        //set date to et_date from Details activity
        et_date.setText(formattedDate);
    }
}