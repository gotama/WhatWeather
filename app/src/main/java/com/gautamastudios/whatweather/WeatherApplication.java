package com.gautamastudios.whatweather;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.gautamastudios.whatweather.logger.WeatherLog;
import com.gautamastudios.whatweather.service.NetworkType;
import com.gautamastudios.whatweather.service.WeatherServiceReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class WeatherApplication extends Application {

    private static final String TAG = WeatherApplication.class.getSimpleName();
    private static WeatherApplication weatherApplication;
    private RequestQueue requestQueue;
    private int jobID = 0;

    public static synchronized WeatherApplication getInstance() {
        return weatherApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        jobID = 0;
        weatherApplication = this;
        scheduleAlarm();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * @param request to add to the queue
     * @param tag     value for canceling request
     * @param <T>     type of Request
     */
    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);
    }

    public static String getKey() {
        return BuildConfig.DARK_KEY;
    }

    /**
     * The weather forecast does not change often, so the API should not be queried more often than every 15 minutes.
     * Instead, the data should be cached in a database or file, and local data should be used to show the forecast if
     * it is fresher than 15 minutes.
     *
     * @param jobService         Service to start
     * @param minLatencyMillis   Milliseconds before which this job will not be considered for execution.
     * @param networkType        Set some description of the kind of network type your job needs to have.
     * @param requiresDeviceIdle Whether or not the device need be within an idle maintenance window.
     * @param requiresCharging   Whether or not the device is plugged in.
     * @param extras             Bundle to pass through to service.
     */
    public void scheduleJob(ComponentName jobService, long minLatencyMillis, @NetworkType int networkType,
            boolean requiresDeviceIdle, boolean requiresCharging, @NonNull PersistableBundle extras) {
        JobInfo.Builder builder = new JobInfo.Builder(jobID++, jobService);

        builder.setMinimumLatency(minLatencyMillis);
        builder.setRequiredNetworkType(networkType);
        builder.setRequiresDeviceIdle(requiresDeviceIdle);
        builder.setRequiresCharging(requiresCharging);
        builder.setExtras(extras);
        builder.setPeriodic(MILLISECONDS.convert(15, MINUTES));

        WeatherLog.d(TAG, "JobSchedulerJournal", " - Scheduling Job");
        WeatherLog.d(TAG, "JobSchedulerJournal", " - jobID : " + jobID);
        WeatherLog.d(TAG, "JobSchedulerJournal", " - jobService : " + jobService.getClassName());
        WeatherLog.d(TAG, "JobSchedulerJournal", " - minLatencyMillis : " + minLatencyMillis);
        WeatherLog.d(TAG, "JobSchedulerJournal", " - networkType : " + networkType);
        WeatherLog.d(TAG, "JobSchedulerJournal", " - requiresDeviceIdle : " + requiresDeviceIdle);
        WeatherLog.d(TAG, "JobSchedulerJournal", " - requiresCharging : " + requiresCharging);

        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(builder.build());
    }

    public boolean isValidJson(String jsonData) {
        boolean retVal;
        try {
            new JSONObject(jsonData);
            retVal = true;
        } catch (JSONException ex) {
            try {
                new JSONArray(jsonData);
                retVal = true;
            } catch (JSONException ex1) {
                retVal = false;
            }
        }

        return retVal;
    }

    /**
     * The app should wake up in the background every 20 minutes (even when the app is not "open"), and get the
     * current temperature. When the current temperature changes from the range 15-25C to above 25C or below 15C, a
     * warning should be shown to the user.
     * <p>
     * Schedule a repeating alarm that has inexact trigger time requirements; for example, an alarm that repeats every
     * hour, but not necessarily at the top of every hour. These alarms are more power-efficient than the strict
     * recurrences traditionally supplied by setRepeating(int, long, long, PendingIntent), since the system can adjust
     * alarms' delivery times to cause them to fire simultaneously, avoiding waking the device from sleep more than
     * necessary.
     * <p>
     * Your alarm's first trigger will not be before the requested time, but it might not occur for almost a full
     * interval after that time. In addition, while the overall period of the repeating alarm will be as requested,
     * the time between any two successive firings of the alarm may vary. If your application demands very low
     * jitter, use one-shot alarms with an appropriate window instead; see setWindow(int, long, long, PendingIntent)
     * and setExact(int, long, PendingIntent).
     */
    public void scheduleAlarm() {
        Intent intent = new Intent(getApplicationContext(), WeatherServiceReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, WeatherServiceReceiver.REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        long twentyMinutes = MILLISECONDS.convert(20, MINUTES);
        WeatherLog.d(TAG, "BackgroundServiceJournal", "scheduleAlarm : " + twentyMinutes);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, twentyMinutes, twentyMinutes, pIntent);
    }
}
