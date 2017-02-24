package com.example.android.criminalintent2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.criminalintent2.database.CrimeDbSchema.CrimeTable;

/**
 * Sqlite open helper to set up database and handle version upgrades
 */

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CrimeTable.NAME + "(" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CrimeTable.Cols.UUID + " INTEGER, " +
                CrimeTable.Cols.TITLE + " TEXT, " +
                CrimeTable.Cols.DATE + " INTEGER, " +
                CrimeTable.Cols.SOLVED + " INTEGER" +
                ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
