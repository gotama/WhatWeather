package com.gautamastudios.whatweather.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gautamastudios.whatweather.api.APICallback;
import com.gautamastudios.whatweather.api.DarkSkyAPI;
import com.gautamastudios.whatweather.logger.WeatherLog;
import com.gautamastudios.whatweather.storage.WeatherDatabase;
import com.gautamastudios.whatweather.storage.model.Alert;
import com.gautamastudios.whatweather.storage.model.DataBlock;
import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.storage.model.DataPointType;
import com.gautamastudios.whatweather.storage.model.Flags;
import com.gautamastudios.whatweather.storage.model.WeatherForecast;
import com.gautamastudios.whatweather.storage.provider.WeatherForecastProvider;
import com.gautamastudios.whatweather.util.GeneralUtils;

import org.json.JSONObject;

import java.util.Date;

import static com.gautamastudios.whatweather.ui.activity.MainActivity.MESSENGER_INTENT_KEY;
import static com.gautamastudios.whatweather.ui.activity.MainActivity.MSG_JOB_STOP;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

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
        shouldSyncTask(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        WeatherLog.d(TAG, "JobSchedulerJournal", " onStopJob : " + params.getJobId());
        return true;
    }

    private void sendMessage(int messageID, @Nullable Object params) {
        if (messenger == null) {
            WeatherLog.d(TAG, "JobSchedulerJournal",
                    "Service is bound, not started. There's no callback to send a message to.");
            return;
        }
        Message m = Message.obtain();
        m.what = messageID;
        m.obj = params;
        try {
            messenger.send(m);
        } catch (RemoteException e) {
            Log.e(TAG, "Error passing service object back to activity.", e);
        }
    }

    /**
     * If no records exist in DB then sync immediately
     */
    private void shouldSyncTask(final JobParameters jobParameters) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                WeatherLog.d(TAG, "SyncJournal", " - shouldSyncTask Started");
                Cursor cursor = WeatherDatabase.getInstance(getApplicationContext()).weatherForecastDao().query();
                if (cursor.moveToFirst() && cursor.getCount() > 0) {
                    WeatherLog.d(TAG, "SyncJournal", " - Content exists");
                    long timestamp = cursor.getLong(cursor.getColumnIndex(WeatherForecast.FIELD_PRIMARY_KEY));

                    Date now = new Date();
                    Date previous = new Date(timestamp * GeneralUtils.ONE_THOUSAND);

                    long MAX_DURATION = MILLISECONDS.convert(15, MINUTES);
                    long duration = now.getTime() - previous.getTime();
                    WeatherLog.d(TAG, "SyncJournal",
                            " - duration : " + duration + " >= " + MAX_DURATION + " : MAX_DURATION");
                    return duration >= MAX_DURATION;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean sync) {
                if (sync) {
                    syncData(jobParameters);
                }
            }
        }.execute();
    }

    private void syncData(final JobParameters jobParameters) {
        DarkSkyAPI.getJsonObjectForecast(new APICallback() {

            @Override
            public void onSuccess(final JSONObject jsonObject) {
                new AsyncTask<Void, Void, Long>() {

                    @Override
                    protected Long doInBackground(Void... params) {
                        getContentResolver().delete(WeatherForecastProvider.getUriProvider(WeatherForecast.TABLE_NAME),
                                null, null);
                        getContentResolver().delete(WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME), null,
                                null);
                        getContentResolver().delete(WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME), null,
                                null);
                        getContentResolver().delete(WeatherForecastProvider.getUriProvider(Alert.TABLE_NAME), null,
                                null);
                        getContentResolver().delete(WeatherForecastProvider.getUriProvider(Flags.TABLE_NAME), null,
                                null);

                        WeatherForecast weatherForecast = WeatherForecast.buildWeatherForecastFromResponse(
                                jsonObject.toString());

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
                                        weatherForecast.getHourly().getData(DataPointType.HOURLY)));

                        getContentResolver().bulkInsert(WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                                WeatherForecastProvider.getBulkInsertDataPointValues(
                                        weatherForecast.getDaily().getData(DataPointType.DAILY)));

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

                        return ContentUris.parseId(itemUri);
                    }

                    @Override
                    protected void onPostExecute(Long timeStampPrimaryKey) {
                        if (timeStampPrimaryKey > 0) {
                            sendMessage(MSG_JOB_STOP, timeStampPrimaryKey);
                            jobFinished(jobParameters, false);
                        } else {
                            WeatherLog.d(TAG, "JobSchedulerJournal", " - FAIL");
                        }
                    }
                }.execute();
            }

            @Override
            public void onFail(String message, int code) {
                WeatherLog.d(TAG, "JobSchedulerJournal", " - DarkSkyAPI onFail");
                if (code == -1) {
                    //TODO Network issues
                }
                jobFinished(jobParameters, false);
            }
        });
    }
}
