package com.example.android.criminalintent2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Chris on 2/10/2017.
 */

public class TimePickerFragment extends DialogFragment {

    public static final String ARG_DATE = "date";

    public static final String EXTRA_TIME =
            "com.bignerdranch.android.criminalintent.time";

    private TimePicker mTimePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_time_picker);
        mTimePicker.setIs24HourView(true);

        // Runtime version checks for deprecated time picker methods
        if (Build.VERSION.SDK_INT >= 23) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minutes);
        } else {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minutes);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int hour;
                        int minutes;

                        if (Build.VERSION.SDK_INT >= 23) {
                            hour = mTimePicker.getHour();
                            minutes = mTimePicker.getMinute();
                        } else {
                            hour = mTimePicker.getCurrentHour();
                            minutes = mTimePicker.getCurrentMinute();
                        }

                        Log.i("TimePickerFragment", "The values are as follows: year-" + year + " month-" + month + " day-" + day + " hour-" + hour + " minutes-" + minutes);

                        Date newDate = new GregorianCalendar(year, month, day, hour, minutes).getTime();

                        sendResult(Activity.RESULT_OK, newDate);
                    }
                })
                .create();
    }


    // For attaching arguments to the dialog fragment
    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     * @param resultCode telling the receiver whether or not the function was successful
     * @param newDate The new date to be updated
     */
    private void sendResult(int resultCode, Date newDate) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, newDate);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
