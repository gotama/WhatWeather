package com.gautamastudios.whatweather.ui.activity;

import android.content.ComponentName;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.gautamastudios.whatweather.R;
import com.gautamastudios.whatweather.WeatherApplication;
import com.gautamastudios.whatweather.logger.WeatherLog;
import com.gautamastudios.whatweather.service.ForecastSyncJobService;
import com.gautamastudios.whatweather.service.NetworkType;
import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.storage.model.DataPointType;
import com.gautamastudios.whatweather.storage.model.WeatherForecast;
import com.gautamastudios.whatweather.storage.provider.WeatherForecastProvider;
import com.gautamastudios.whatweather.ui.adapter.WeatherViewPageAdapter;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int GET_WEATHER_FORECAST = 1;
    private static final int GET_CURRENT_DATA_POINT = 2;
    private static final int GET_HOURLY_DATA_POINT = 3;
    private static final int GET_DAILY_DATA_POINT = 4;
    private static final int GET_DATA_BLOCK = 5;
    private static final int GET_ALERT = 6;
    private static final int GET_FLAG = 7;

    public static final int MSG_CURRENT_CURSOR_CLOSED = 230;
    public static final int MSG_HOURLY_CURSOR_CLOSED = 231;
    public static final int MSG_DAILY_CURSOR_CLOSED = 232;

    private IncomingMessageHandler incomingMessageHandler;

    ViewPager viewPager;
    WeatherViewPageAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_activity_main);
        incomingMessageHandler = new IncomingMessageHandler(this);

        viewPager = findViewById(R.id.viewpager);
        viewPagerAdapter = new WeatherViewPageAdapter(this, new Messenger(incomingMessageHandler));
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle args = new Bundle();
        args.putInt(DataPoint.FIELD_DATA_POINT_TYPE, DataPointType.CURRENTLY);
        getSupportLoaderManager().initLoader(GET_CURRENT_DATA_POINT, args, loaderCallbacks);

        args = new Bundle();
        args.putInt(DataPoint.FIELD_DATA_POINT_TYPE, DataPointType.HOURLY);
        getSupportLoaderManager().initLoader(GET_HOURLY_DATA_POINT, args, loaderCallbacks);

        args = new Bundle();
        args.putInt(DataPoint.FIELD_DATA_POINT_TYPE, DataPointType.DAILY);
        getSupportLoaderManager().initLoader(GET_DAILY_DATA_POINT, args, loaderCallbacks);

        WeatherApplication.getInstance().scheduleJob(new ComponentName(this, ForecastSyncJobService.class), 0,
                NetworkType.NETWORK_TYPE_ANY, false, false, new PersistableBundle());
    }

    @Override
    protected void onDestroy() {
        getSupportLoaderManager().destroyLoader(GET_CURRENT_DATA_POINT);
        getSupportLoaderManager().destroyLoader(GET_HOURLY_DATA_POINT);
        getSupportLoaderManager().destroyLoader(GET_DAILY_DATA_POINT);
        super.onDestroy();
    }

    public final LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case GET_WEATHER_FORECAST:
                    Uri uri;
                    if (args != null) {
                        uri = ContentUris.withAppendedId(
                                WeatherForecastProvider.getUriProvider(WeatherForecast.TABLE_NAME),
                                args.getLong(WeatherForecast.FIELD_PRIMARY_KEY));
                    } else {
                        uri = WeatherForecastProvider.getUriProvider(WeatherForecast.TABLE_NAME);
                    }

                    return new CursorLoader(getApplicationContext(), uri,
                            new String[]{WeatherForecast.FIELD_PRIMARY_KEY}, null, null, null);
                case GET_CURRENT_DATA_POINT:
                case GET_HOURLY_DATA_POINT:
                case GET_DAILY_DATA_POINT:
                    String dataType = "";
                    if (args != null) {
                        dataType = String.valueOf(args.getInt(DataPoint.FIELD_DATA_POINT_TYPE));
                    }

                    return new CursorLoader(getApplicationContext(),
                            WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                            new String[]{DataPoint.FIELD_PRIMARY_KEY}, dataType, null, null);
                case GET_DATA_BLOCK:
                    return null;
                case GET_ALERT:
                    return null;
                case GET_FLAG:
                    return null;
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            switch (loader.getId()) {
                case GET_WEATHER_FORECAST:
                    break;
                case GET_CURRENT_DATA_POINT:
                    viewPagerAdapter.setCurrentCursor(data);
                    break;
                case GET_HOURLY_DATA_POINT:
                    viewPagerAdapter.setHourlyCursor(data);
                    break;
                case GET_DAILY_DATA_POINT:
                    viewPagerAdapter.setDailyCursor(data);
                    break;
                case GET_DATA_BLOCK:
                    break;
                case GET_ALERT:
                    break;
                case GET_FLAG:
                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            switch (loader.getId()) {
                case GET_WEATHER_FORECAST:
                    break;
                case GET_DAILY_DATA_POINT:
                    break;
                case GET_CURRENT_DATA_POINT:
                    break;
                case GET_HOURLY_DATA_POINT:
                    break;
                case GET_DATA_BLOCK:
                    break;
                case GET_ALERT:
                    break;
                case GET_FLAG:
                    break;
            }
        }

    };

    /**
     * A {@link IncomingMessageHandler} allows you to send messages associated with a thread. A {@link Messenger}
     * uses this handler to communicate from {@link ForecastSyncJobService}.
     */
    private static class IncomingMessageHandler extends Handler {

        private WeakReference<MainActivity> activityWeakReference;

        IncomingMessageHandler(MainActivity activity) {
            super();
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = activityWeakReference.get();
            if (mainActivity == null) {
                WeatherLog.d(TAG, "UIJournal", "Activity is no longer available, exit.");
                return;
            }
            Bundle args;
            switch (msg.what) {
                case MSG_CURRENT_CURSOR_CLOSED:
                    args = new Bundle();
                    args.putInt(DataPoint.FIELD_DATA_POINT_TYPE, DataPointType.CURRENTLY);
                    activityWeakReference.get().getSupportLoaderManager().restartLoader(GET_CURRENT_DATA_POINT, args,
                            mainActivity.loaderCallbacks);
                    break;
                case MSG_HOURLY_CURSOR_CLOSED:
                    args = new Bundle();
                    args.putInt(DataPoint.FIELD_DATA_POINT_TYPE, DataPointType.HOURLY);
                    activityWeakReference.get().getSupportLoaderManager().restartLoader(GET_HOURLY_DATA_POINT, args,
                            mainActivity.loaderCallbacks);
                    break;
                case MSG_DAILY_CURSOR_CLOSED:
                    WeatherLog.d(TAG, "CursorJournal", "MSG_DAILY_CURSOR_CLOSED");
                    args = new Bundle();
                    args.putInt(DataPoint.FIELD_DATA_POINT_TYPE, DataPointType.DAILY);
                    activityWeakReference.get().getSupportLoaderManager().restartLoader(GET_DAILY_DATA_POINT, args,
                            mainActivity.loaderCallbacks);
                    break;
            }
        }
    }
}
