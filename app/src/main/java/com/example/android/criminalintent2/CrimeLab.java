package com.example.android.criminalintent2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.criminalintent2.database.CrimeBaseHelper;
import com.example.android.criminalintent2.database.CrimeCursorWrapper;
import com.example.android.criminalintent2.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stores data as a singleton while application is in memory. Will be destroyed after
 * app is stopped and memory is released
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

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
        ContentValues values = getContentValues(c);

        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + "=?",
                new String[]{uuidString});
    }

    public void deleteCrime(Crime crime) {
        mDatabase.delete(
                CrimeTable.NAME,
                CrimeTable.Cols.UUID + "=?",
                new String[] {crime.getId().toString()}
        );
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID id) {
        // otherwise no crime by that id
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + "=?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeTable.Cols.SUSPECT_ID, Long.toString(crime.getSuspectId()));

        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, //groupBy
                null, //having
                null // orderBy
        );

        return new CrimeCursorWrapper(cursor);
    }

    /**
     * Private constructor prevents more than one instance
     * and can only be created from this class
     */
    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();
    }
}
