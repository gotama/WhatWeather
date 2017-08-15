package com.gautamastudios.whatweather.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.gautamastudios.whatweather.api.APICallback;
import com.gautamastudios.whatweather.api.DarkSkyAPI;
import com.gautamastudios.whatweather.logger.WeatherLog;
import com.gautamastudios.whatweather.storage.model.WeatherForecast;

import org.json.JSONObject;

public class WeatherService extends IntentService {

    private static final String TAG = WeatherService.class.getSimpleName();

    public WeatherService() {
        super("weather-service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //TODO add in app pop up
        //        //temperatue = ((temperatue - 32)*5)/9;
        //        final double oldTempC;
        //        double temperatureF;
        //        Cursor currentDataPoint = WeatherDatabase.getInstance(getApplicationContext()).dataPointDao()
        //                .queryDataPointsWhere(DataPointType.CURRENTLY);
        //        if (currentDataPoint.moveToFirst() && currentDataPoint.getCount() > 0) {
        //            temperatureF = currentDataPoint.getDouble(
        //                    currentDataPoint.getColumnIndexOrThrow(DataPoint.FIELD_TEMPERATURE));
        //            oldTempC = ((temperatureF - 32) * 5) / 9;
        //        }
        //popUp dialog if in app

        WeatherLog.d(TAG, "BackgroundServiceJournal", "onHandleIntent received");
        DarkSkyAPI.getJsonObjectForecast(new APICallback() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                WeatherForecast weatherForecast = WeatherForecast.buildWeatherForecastFromResponse(
                        jsonObject.toString());
                double tempInF = weatherForecast.getCurrently().temperature;
                double newTempC = ((tempInF - 32) * 5) / 9;

                if (newTempC > 25) {
                    //TODO createNotification

                } else if (newTempC < 15) {

                }
            }

            @Override
            public void onFail(String message, int code) {

            }
        });
    }
}
