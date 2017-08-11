package com.gautamastudios.whatweather.util;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public final class GeneralUtils {

    private static final String TAG = GeneralUtils.class.getSimpleName();

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

    public static Date convertUnixTime(int unixTime) {
        return new Date((long) unixTime * 1000);
    }
}
