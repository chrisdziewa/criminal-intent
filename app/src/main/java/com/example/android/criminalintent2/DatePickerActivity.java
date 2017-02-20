package com.example.android.criminalintent2;

import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;

/**
 * Created by Chris on 2/14/2017.
 */

public class DatePickerActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {

        Date date = (Date) getIntent().getSerializableExtra(CrimeFragment.EXTRA_DATE);
        return DatePickerFragment.newInstance(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
