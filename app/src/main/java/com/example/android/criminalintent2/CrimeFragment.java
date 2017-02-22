package com.example.android.criminalintent2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Chris on 1/31/2017.
 */

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";

    // Dialog tags
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    // Activity extras
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_TIME = "time";

    public static final int ACTIVITY_REQUEST_DATE = 3;
    public static final int ACTIVITY_REQUEST_TIME = 4;
    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_TIME = 1;


    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        setHasOptionsMenu(true);
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Also intentionally left blank
            }
        });

        // Setup date button
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        final Date currentDate = mCrime.getDate();

        String formattedDate = DateFormatter.formatDateAsString(currentDate);
        mDateButton.setText(formattedDate);

        // Setup time button
        mTimeButton = (Button) v.findViewById(R.id.crime_time_button);
        String formattedTime = DateFormatter.formatDateAsTimeString(currentDate);
        mTimeButton.setText(formattedTime);

        // Button on click listeners
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTablet(getContext())) {
                    Log.i("CrimeFragment", "Device is a tablet, show dialog");
                    FragmentManager manager = getFragmentManager();
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    dialog.show(manager, DIALOG_DATE);
                } else {
                    // Screen is smaller - display dialog as full screen activity
                    Intent intent = new Intent(getContext(), DatePickerActivity.class);
                    intent.putExtra(EXTRA_DATE, mCrime.getDate());
                    startActivityForResult(intent, ACTIVITY_REQUEST_DATE);
                }
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTablet(getContext())) {
                    Log.i("CrimeFragment", "Device is a tablet, show dialog");
                    FragmentManager manager = getFragmentManager();
                    TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                    dialog.show(manager, DIALOG_TIME);
                } else {
                    // Screen is smaller - display dialog as full screen activity
                    Intent intent = new Intent(getContext(), TimePickerActivity.class);
                    intent.putExtra(EXTRA_TIME, mCrime.getDate());
                    startActivityForResult(intent, ACTIVITY_REQUEST_TIME);
                }
            }
        });

        // Setup checkbox
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        return v;
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE || requestCode == REQUEST_TIME ||
                requestCode == ACTIVITY_REQUEST_DATE || requestCode == ACTIVITY_REQUEST_TIME) {
            final Date date;
            if (requestCode == REQUEST_DATE || requestCode == ACTIVITY_REQUEST_DATE) {
                date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            } else {
                date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            }

            mCrime.setDate(date);
            mDateButton.setText(DateFormatter.formatDateAsString(date));
            mTimeButton.setText(DateFormatter.formatDateAsTimeString(date));
        }
    }

    // For determining dialog as dialog or fragment
    // from http://www.androidcodesnippets.com/2016/02/check-if-device-is-tablet-or-phone/
    private boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(ARG_CRIME_ID, mCrime.getId());

        super.onSaveInstanceState(outState);
    }

    private void deleteCrime() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        crimeLab.deleteCrime(mCrime);

        Toast.makeText(getActivity(), R.string.toast_delete_crime, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                deleteCrime();
                getActivity().finish();
                return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
