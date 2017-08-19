package com.gautamastudios.whatweather.ui.activity;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gautamastudios.whatweather.BuildConfig;
import com.gautamastudios.whatweather.R;
import com.gautamastudios.whatweather.WeatherApplication;
import com.gautamastudios.whatweather.logger.WeatherLog;
import com.gautamastudios.whatweather.service.ForecastSyncJobService;
import com.gautamastudios.whatweather.service.NetworkType;
import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.storage.model.DataPointType;
import com.gautamastudios.whatweather.storage.model.WeatherForecast;
import com.gautamastudios.whatweather.storage.provider.WeatherForecastProvider;
import com.gautamastudios.whatweather.ui.adapter.HorizontalHourlyAdapter;
import com.gautamastudios.whatweather.ui.adapter.WeatherViewPageAdapter;
import com.gautamastudios.whatweather.util.GeneralUtils;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int GET_WEATHER_FORECAST = 1;
    private static final int GET_CURRENT_DATA_POINT = 2;
    private static final int GET_HOURLY_DATA_POINT = 3;
    private static final int GET_DAILY_DATA_POINT = 4;
    private static final int GET_DATA_BLOCK = 5;
    private static final int GET_ALERT = 6;
    private static final int GET_FLAG = 7;

    public static final int MSG_JOB_STOP = 230;

    public static final String MESSENGER_INTENT_KEY = BuildConfig.APPLICATION_ID + ".MESSENGER_INTENT_KEY";

    private IncomingMessageHandler incomingMessageHandler;

    ViewPager viewPager;
    WeatherViewPageAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_activity_main);

        viewPager = findViewById(R.id.viewpager);
        viewPagerAdapter = new WeatherViewPageAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        incomingMessageHandler = new IncomingMessageHandler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, ForecastSyncJobService.class);
        Messenger messengerIncoming = new Messenger(incomingMessageHandler);
        startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming);
        startService(startServiceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle args = new Bundle();
        args.putInt(DataPoint.FIELD_DATA_POINT_TYPE, DataPointType.HOURLY);
        getSupportLoaderManager().initLoader(GET_HOURLY_DATA_POINT, args, loaderCallbacks);

        args = new Bundle();
        args.putInt(DataPoint.FIELD_DATA_POINT_TYPE, DataPointType.CURRENTLY);
        getSupportLoaderManager().initLoader(GET_CURRENT_DATA_POINT, args, loaderCallbacks);

        WeatherApplication.getInstance().scheduleJob(new ComponentName(this, ForecastSyncJobService.class), 0,
                NetworkType.NETWORK_TYPE_ANY, false, false, new PersistableBundle());
    }

    @Override
    protected void onDestroy() {
        getSupportLoaderManager().destroyLoader(GET_HOURLY_DATA_POINT);
        getSupportLoaderManager().destroyLoader(GET_CURRENT_DATA_POINT);
        super.onDestroy();
    }

    //TODO
    //    private void setupRecyclerView() {
    //        final Context context = recyclerView.getContext();
    //        final int spacing = getResources().getDimensionPixelOffset(R.dimen.default_spacing_small);
    //        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    //        recyclerView.setAdapter(dailyCardAdapter);
    //        recyclerView.addItemDecoration(new ItemOffsetDecoration(spacing));
    //    }
    //
    //    private void runLayoutAnimation(final RecyclerView recyclerView, final int animationResourceID) {
    //        final Context context = recyclerView.getContext();
    //
    //        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context,
    // animationResourceID);
    //
    //        recyclerView.setLayoutAnimation(controller);
    //        recyclerView.getAdapter().notifyDataSetChanged();
    //        recyclerView.scheduleLayoutAnimation();
    //    }

    private final LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

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
                    View currentView = viewPager.findViewWithTag(WeatherViewPageAdapter.WeatherPage.TODAY.getId());

                    if (data.moveToFirst()) {
                        do {
                            TextView currentTime = currentView.findViewById(R.id.current_time);
                            //TODO hi lo works only off daily
                            TextView currentHiLo = currentView.findViewById(R.id.current_hi_lo);
                            TextView currentTemp = currentView.findViewById(R.id.current_temp);
                            TextView currentFeels = currentView.findViewById(R.id.current_feels_like);
                            TextView currentSummary = currentView.findViewById(R.id.current_summary);
                            ImageView currentIcon = currentView.findViewById(R.id.current_weather_icon);

                            currentTime.setText(GeneralUtils
                                    .convertUnixTimeToDate(data.getLong(data.getColumnIndex(DataPoint.FIELD_TIME))));

                            currentTemp.setText(GeneralUtils.convertDoubleToTemp(
                                    data.getDouble(data.getColumnIndex(DataPoint.FIELD_TEMPERATURE))));

                            String icon = data.getString(data.getColumnIndex(DataPoint.FIELD_ICON));
                            currentIcon.setImageDrawable(ContextCompat
                                    .getDrawable(getBaseContext(), DataPoint.Icon.getIcon(icon).getResourceId()));

                            currentHiLo.setText(String.format(Locale.getDefault(), "Day %1$s - Night %2$s", GeneralUtils
                                            .convertDoubleToTemp(data.getDouble(
                                                    data.getColumnIndex(DataPoint.FIELD_APPARENT_TEMPERATURE_MAX))),
                                    GeneralUtils.convertDoubleToTemp(data.getDouble(
                                            data.getColumnIndex(DataPoint.FIELD_APPARENT_TEMPERATURE_MIN)))));

                            currentFeels.setText(String.format(Locale.getDefault(), "Feels like %s", GeneralUtils
                                    .convertDoubleToTemp(data.getDouble(
                                            data.getColumnIndex(DataPoint.FIELD_APPARENT_TEMPERATURE)))));

                            currentSummary.setText(data.getString(data.getColumnIndex(DataPoint.FIELD_SUMMARY)));
                        } while (data.moveToNext());

                    }

                    viewPagerAdapter.setCurrentCursor(data);
                    break;
                case GET_HOURLY_DATA_POINT:
                    View hourlyView = viewPager.findViewWithTag(WeatherViewPageAdapter.WeatherPage.TODAY.getId());

                    RecyclerView recyclerView = hourlyView.findViewById(R.id.hourly_recycler_view);
                    if (recyclerView != null) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(),
                                LinearLayoutManager.HORIZONTAL, false);

                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(new HorizontalHourlyAdapter(getBaseContext(), data));
                    }

                    viewPagerAdapter.setHourlyCursor(data);
                    break;
                case GET_DAILY_DATA_POINT:
                    //                    dailyCardAdapter.setDailyCursor(data);
                    //                    runLayoutAnimation(recyclerView, R.anim.layout_animation_from_right);
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
                    viewPagerAdapter.setCurrentCursor(null);
                    break;
                case GET_HOURLY_DATA_POINT:
                    viewPagerAdapter.setHourlyCursor(null);
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
            switch (msg.what) {
                case MSG_JOB_STOP:
                    Bundle args = new Bundle();
                    args.putInt(DataPoint.FIELD_DATA_POINT_TYPE, DataPointType.HOURLY);
                    activityWeakReference.get().getSupportLoaderManager().restartLoader(GET_HOURLY_DATA_POINT, args,
                            mainActivity.loaderCallbacks);

                    args = new Bundle();
                    args.putInt(DataPoint.FIELD_DATA_POINT_TYPE, DataPointType.CURRENTLY);
                    activityWeakReference.get().getSupportLoaderManager().restartLoader(GET_CURRENT_DATA_POINT, args,
                            mainActivity.loaderCallbacks);
                    break;
            }
        }
    }
}
