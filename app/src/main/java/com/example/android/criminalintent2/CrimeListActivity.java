package com.example.android.criminalintent2;

import android.support.v4.app.Fragment;

/**
 * Created by Chris on 2/2/2017.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
