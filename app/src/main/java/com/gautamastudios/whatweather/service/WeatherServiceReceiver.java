package com.gautamastudios.whatweather.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gautamastudios.whatweather.logger.WeatherLog;

public class WeatherServiceReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 7772890;
    private static final String TAG = WeatherServiceReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        WeatherLog.d(TAG, "BackgroundServiceJournal", "onReceive starting WeatherService");
        context.startService(new Intent(context, WeatherService.class));
    }
}
