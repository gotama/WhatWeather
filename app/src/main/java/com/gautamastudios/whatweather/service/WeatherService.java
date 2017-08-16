package com.gautamastudios.whatweather.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.gautamastudios.whatweather.R;
import com.gautamastudios.whatweather.api.APICallback;
import com.gautamastudios.whatweather.api.DarkSkyAPI;
import com.gautamastudios.whatweather.logger.WeatherLog;
import com.gautamastudios.whatweather.notification.NotificationManager;
import com.gautamastudios.whatweather.storage.model.WeatherForecast;

import org.json.JSONObject;

public class WeatherService extends IntentService {

    private static final String TAG = WeatherService.class.getSimpleName();

    public WeatherService() {
        super("weather-service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        WeatherLog.d(TAG, "BackgroundServiceJournal", "onHandleIntent received");
        DarkSkyAPI.getJsonObjectForecast(new APICallback() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                WeatherForecast weatherForecast = WeatherForecast.buildWeatherForecastFromResponse(
                        jsonObject.toString());
                double temperature = weatherForecast.getCurrently().temperature;

                NotificationManager notificationManager = new NotificationManager(getApplicationContext());

                if (temperature > 25) {
                    WeatherLog.d(TAG, "BackgroundServiceJournal", "Temperature is above 25");
                    notificationManager.setNormalStyle("Temperature is above 25",
                            "http://gautamastudios.com/assets/rain.png", R.drawable.ic_stat_whatshot);

                } else if (temperature < 15) {
                    WeatherLog.d(TAG, "BackgroundServiceJournal", "Temperature is below 15");

                    notificationManager.setNormalStyle("Temperature is below 15",
                            "http://gautamastudios.com/assets/rain.png", R.drawable.ic_stat_ac_unit);
                }
            }

            @Override
            public void onFail(String message, int code) {

            }
        }, new String[]{DarkSkyAPI.EXCLUDE_MINUTELY, DarkSkyAPI.EXCLUDE_HOURLY, DarkSkyAPI.EXCLUDE_DAILY,
                DarkSkyAPI.EXCLUDE_ALERTS, DarkSkyAPI.EXCLUDE_FLAGS});
    }

}
