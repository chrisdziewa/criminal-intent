package com.example.android.criminalintent2;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stores data as a singleton while application is in memory. Will be destroyed after
 * app is stopped and memory is released
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private List<Crime> mCrimes;

    /**
     * @param context of the application state
     * @return existing or new CrimeLab (If one doesn't exist)
     */
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }

        return sCrimeLab;
    }

    // Adds a crime to the list of crimes
    public void addCrime(Crime c) {
        mCrimes.add(c);
    }

    public void deleteCrime(Crime crime) {
        mCrimes.remove(crime);
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        // otherwise no crime by that id
        return null;
    }

    /**
     * Private constructor prevents more than one instance
     * and can only be created from this class
     */
    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
    }
}
