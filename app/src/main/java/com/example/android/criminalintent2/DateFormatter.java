package com.example.android.criminalintent2;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Helper method created by Chris - not included in book tutorial
 */

public class DateFormatter {
    /**
     *
     * @param dateStyle an integer representing the formatting from DateFormat Library
     *                  ex: DateFormat.FULL, DateFormat.SHORT
     * @param date a standard date object
     * @return a nicely formatted date string based on locale
     */
    public static String formatDateAsString(int dateStyle, Date date) {
        DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.getDefault());
        return formatter.format(date);
    }

    /**
     *
     * @param dateStyle an integer representing the formatting from DateFormat Library
     *                  ex: DateFormat.FULL, DateFormat.SHORT
     * @param date a standard date object
     * @return a nicely formatted time string based on locale
     */
    public static String formatDateAsTimeString(int dateStyle, Date date) {
        DateFormat formatter = DateFormat.getTimeInstance(dateStyle, Locale.getDefault());
        return formatter.format(date);
    }
}
