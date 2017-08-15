package com.gautamastudios.whatweather.logger;

import android.util.Log;

public class WeatherLog {

    public static void d(String tag, String category, String msg) {
        Log.d(tag, category + " : " + msg);
    }

    public static void d(String tag, String category, String msg, Throwable tr) {
        Log.d(tag, category + " : " + msg, tr);
    }
}
