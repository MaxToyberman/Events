package com.toyberman.wedding.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TimePicker;

import com.toyberman.wedding.R;

import java.util.Calendar;

/**
 * Created by Toyberman Maxim on 9/7/15.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    private int hour;
    private int minute;

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        //int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minutes, true);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        setHour(hour);
        setMinute(minute);
        NewEventFragment.mCalendar.set(Calendar.HOUR_OF_DAY,hour);
        NewEventFragment.mCalendar.set(Calendar.MINUTE,minute);
        String formattedHour =String.format("%02d:%02d", hour,minute);
        EditText et_hour= (EditText) getActivity().findViewById(R.id.et_time);
        et_hour.setText(formattedHour);


    }
}
