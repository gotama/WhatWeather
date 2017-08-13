package com.gautamastudios.whatweather.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gautamastudios.whatweather.api.APICallback;
import com.gautamastudios.whatweather.api.DarkSkyAPI;
import com.gautamastudios.whatweather.storage.model.Alert;
import com.gautamastudios.whatweather.storage.model.DataBlock;
import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.storage.model.DataPointType;
import com.gautamastudios.whatweather.storage.model.Flags;
import com.gautamastudios.whatweather.storage.model.WeatherForecast;
import com.gautamastudios.whatweather.storage.provider.WeatherForecastProvider;

import org.json.JSONObject;

import static com.gautamastudios.whatweather.MainActivity.MESSENGER_INTENT_KEY;
import static com.gautamastudios.whatweather.MainActivity.MSG_JOB_STOP;
import static com.gautamastudios.whatweather.WeatherApplication.LOG_JOB_SCHEDULER;

/**
 * Service that initiates a network call and on the successful response persists the data to Android new architecture
 * component database Room through a ContentProvider.
 */
public class ForecastSyncJobService extends JobService {

    private static final String TAG = ForecastSyncJobService.class.getSimpleName();

    private Messenger messenger;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        messenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY);
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.d(TAG, LOG_JOB_SCHEDULER + " - onStartJob");

        DarkSkyAPI.getJsonObjectForecast(new APICallback() {

            @Override
            public void onSuccess(final JSONObject jsonObject) {

                Log.d(TAG, LOG_JOB_SCHEDULER + " - API response onSuccess");

                new AsyncTask<Void, Void, Long>() {

                    @Override
                    protected Long doInBackground(Void... params) {
                        Log.d(TAG, LOG_JOB_SCHEDULER + " - clearing database");
                        getContentResolver().delete(WeatherForecastProvider.getUriProvider(WeatherForecast.TABLE_NAME),
                                null, null);

                        WeatherForecast weatherForecast = WeatherForecast.buildWeatherForecastFromResponse(
                                jsonObject.toString());

                        Log.d(TAG, LOG_JOB_SCHEDULER + " - inserting data");
                        final Uri itemUri = getContentResolver().insert(
                                WeatherForecastProvider.getUriProvider(WeatherForecast.TABLE_NAME),
                                WeatherForecastProvider.getWeatherForecastValues(weatherForecast));

                        getContentResolver().insert(WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                                WeatherForecastProvider.getDataPointValues(weatherForecast.getCurrently()));

                        getContentResolver().bulkInsert(WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                                WeatherForecastProvider.getBulkInsertDataPointValues(
                                        weatherForecast.getMinutely().getData(DataPointType.MINUTELY)));

                        getContentResolver().bulkInsert(WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                                WeatherForecastProvider.getBulkInsertDataPointValues(
                                        weatherForecast.getMinutely().getData(DataPointType.HOURLY)));

                        getContentResolver().bulkInsert(WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                                WeatherForecastProvider.getBulkInsertDataPointValues(
                                        weatherForecast.getMinutely().getData(DataPointType.DAILY)));

                        getContentResolver().insert(WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME),
                                WeatherForecastProvider.getDataBlockValues(weatherForecast.getMinutely()));

                        getContentResolver().insert(WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME),
                                WeatherForecastProvider.getDataBlockValues(weatherForecast.getHourly()));

                        getContentResolver().insert(WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME),
                                WeatherForecastProvider.getDataBlockValues(weatherForecast.getDaily()));

                        getContentResolver().bulkInsert(WeatherForecastProvider.getUriProvider(Alert.TABLE_NAME),
                                WeatherForecastProvider.getBulkInsertAlertValues(weatherForecast.getAlerts()));

                        getContentResolver().insert(WeatherForecastProvider.getUriProvider(Flags.TABLE_NAME),
                                WeatherForecastProvider.getFlagsValues(weatherForecast.getFlags()));
                        Log.d(TAG, LOG_JOB_SCHEDULER + " - inserting data finished");

                        return ContentUris.parseId(itemUri);
                    }

                    @Override
                    protected void onPostExecute(Long timeStampPrimaryKey) {
                        if (timeStampPrimaryKey > 0) {
                            Log.d(TAG, LOG_JOB_SCHEDULER + " - sending message to MainActivity and finishing service");
                            sendMessage(MSG_JOB_STOP, timeStampPrimaryKey);
                            jobFinished(jobParameters, false);
                        } else {
                            Log.d(TAG, LOG_JOB_SCHEDULER + " - FAIL");
                        }
                    }
                }.execute();
            }

            @Override
            public void onFail(String message, int code) {
                Log.d(TAG, LOG_JOB_SCHEDULER + " - DarkSkyAPI onFail");
                if (code == -1) {
                    //TODO Network issues
                }
                jobFinished(jobParameters, false);
            }
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, LOG_JOB_SCHEDULER + " onStopJob : " + params.getJobId());
        return true;
    }

    private void sendMessage(int messageID, @Nullable Object params) {
        if (messenger == null) {
            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.");
            return;
        }
        Message m = Message.obtain();
        m.what = messageID;
        m.obj = params;
        try {
            messenger.send(m);
        } catch (RemoteException e) {
            Log.e(TAG, "Error passing service object back to activity.");
        }
    }
}
