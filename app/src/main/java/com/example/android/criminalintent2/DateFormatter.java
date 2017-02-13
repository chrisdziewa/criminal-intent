package com.example.android.criminalintent2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Helper method created by Chris - not included in book tutorial
 */

public class DateFormatter {
    public static String formatDateAsString(Date date) {
        String dateFormat = "EEEE, MMM dd, yyyy";

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.US);
        return formatter.format(date);
    }

    public static String formatDateAsTimeString(Date date) {
        String timeFormat = "HH:mma";

        SimpleDateFormat formatter = new SimpleDateFormat(timeFormat, Locale.US);
        return formatter.format(date);
    }
}
