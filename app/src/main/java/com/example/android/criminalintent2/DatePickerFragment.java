package com.example.android.criminalintent2;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A date picker alert dialog with a Dialog Fragment superclass wrapper
 */
public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE =
            "com.bignerdranch.android.criminalintent.date";

    private DatePicker mDatePicker;
    private Button mPositiveButton;
    private Button mNegativeButton;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Date startingDate = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(startingDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // Fill in existing time so it isn't replaced by 0:00 every time
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minutes = calendar.get(Calendar.MINUTE);

        View v = inflater.inflate(R.layout.dialog_date, null);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null);

        mPositiveButton = (Button) v.findViewById(R.id.save_button);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();
                Date date = new GregorianCalendar(year, month, day, hour, minutes).getTime();
                sendResult(Activity.RESULT_OK, date);
            }
        });

        mNegativeButton = (Button) v.findViewById(R.id.cancel_button);
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getTargetFragment() != null) {
                    dismiss();
                } else {
                    getActivity().finish();
                }
            }
        });

        return v;
    }

    private void sendResult(int resultCode, Date date) {

        if (getTargetFragment() != null) {
            // send back to fragment
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATE, date);
            getTargetFragment()
                    .onActivityResult(getTargetRequestCode(), resultCode, intent);
            dismiss();
            return;
        }

        // Otherwise fragment was called from an activity
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getActivity().setResult(resultCode, intent);
        getActivity().finish();

    }
}
