package com.gautamastudios.whatweather;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.gautamastudios.whatweather.service.NetworkType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherApplication extends Application {

    public static final String LOG_JOB_SCHEDULER = "JobScheduler";
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
     * @param jobService              Service to start
     * @param minLatencyMillis        Milliseconds before which this job will not be considered for execution.
     * @param maxExecutionDelayMillis Set deadline which is the maximum scheduling latency. There is a API bug where
     *                                the job can run a second time if this value is to low.
     * @param networkType             Set some description of the kind of network type your job needs to have.
     * @param requiresDeviceIdle      Whether or not the device need be within an idle maintenance window.
     * @param requiresCharging        Whether or not the device is plugged in.
     * @param extras                  Bundle to pass through to service.
     */
    public void scheduleJob(ComponentName jobService, long minLatencyMillis, long maxExecutionDelayMillis,
            @NetworkType int networkType, boolean requiresDeviceIdle, boolean requiresCharging,
            @NonNull PersistableBundle extras) {
        JobInfo.Builder builder = new JobInfo.Builder(jobID++, jobService);

        builder.setMinimumLatency(minLatencyMillis);
        builder.setOverrideDeadline(maxExecutionDelayMillis);
        builder.setRequiredNetworkType(networkType);
        builder.setRequiresDeviceIdle(requiresDeviceIdle);
        builder.setRequiresCharging(requiresCharging);
        builder.setExtras(extras);

        Log.d(TAG, LOG_JOB_SCHEDULER + " - Scheduling Job");
        Log.d(TAG, LOG_JOB_SCHEDULER + " - jobID : " + jobID);
        Log.d(TAG, LOG_JOB_SCHEDULER + " - jobService : " + jobService.getClassName());
        Log.d(TAG, LOG_JOB_SCHEDULER + " - minLatencyMillis : " + minLatencyMillis);
        Log.d(TAG, LOG_JOB_SCHEDULER + " - maxExecutionDelayMillis : " + maxExecutionDelayMillis);
        Log.d(TAG, LOG_JOB_SCHEDULER + " - networkType : " + networkType);
        Log.d(TAG, LOG_JOB_SCHEDULER + " - requiresDeviceIdle : " + requiresDeviceIdle);
        Log.d(TAG, LOG_JOB_SCHEDULER + " - requiresCharging : " + requiresCharging);

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
}
