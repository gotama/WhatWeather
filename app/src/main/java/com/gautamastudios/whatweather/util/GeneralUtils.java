package com.gautamastudios.whatweather.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class GeneralUtils {

    private static final String TAG = GeneralUtils.class.getSimpleName();
    public static final String UNIX_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
    public static final String UNIX_TIME_FORMAT = "HH:mm";
    public static final long ONE_THOUSAND = 1000L;
    private static char DEGREE_SYMBOL = '\u00B0';

    public static String readFileFromAssets(String fileName, Context context) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String text = new String(buffer);
            return text;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static String convertUnixTimeToDate(long unixTime) {
        return new SimpleDateFormat(UNIX_DATE_FORMAT, Locale.US).format(new Date(unixTime * ONE_THOUSAND));
    }

    public static String convertUnixTimeToTime(long unixTime) {
        return new SimpleDateFormat(UNIX_TIME_FORMAT, Locale.US).format(new Date(unixTime * ONE_THOUSAND));
    }

    public static String convertDoubleToTemp(double temp) {
        return String.valueOf(Math.round(temp)) + DEGREE_SYMBOL;
    }

    public static int convertDPtoPX(int dpValue, Context context) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, r.getDisplayMetrics());
    }
}
