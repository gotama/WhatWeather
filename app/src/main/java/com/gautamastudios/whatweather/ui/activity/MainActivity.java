package com.gautamastudios.whatweather.ui.activity;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

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
import com.gautamastudios.whatweather.ui.adapter.DailyCardAdapter;
import com.gautamastudios.whatweather.ui.animation.ItemOffsetDecoration;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int GET_WEATHER_FORECAST = 1;
    private static final int GET_DATA_POINT = 2;
    private static final int GET_DATA_BLOCK = 3;
    private static final int GET_ALERT = 4;
    private static final int GET_FLAG = 5;

    public static final int MSG_JOB_STOP = 230;

    public static final String MESSENGER_INTENT_KEY = BuildConfig.APPLICATION_ID + ".MESSENGER_INTENT_KEY";

    private DailyCardAdapter dailyCardAdapter;
    private IncomingMessageHandler incomingMessageHandler;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        incomingMessageHandler = new IncomingMessageHandler(this);

        recyclerView = findViewById(R.id.daily_recycler_view);
        dailyCardAdapter = new DailyCardAdapter();
        setupRecyclerView();
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
        WeatherLog.d(TAG, "LoaderJournal", "initLoader called from onResume");
        getSupportLoaderManager().initLoader(GET_DATA_POINT, null, loaderCallbacks);
        WeatherApplication.getInstance().scheduleJob(new ComponentName(this, ForecastSyncJobService.class), 0,
                NetworkType.NETWORK_TYPE_ANY, false, false, new PersistableBundle());
    }

    @Override
    protected void onDestroy() {
        getSupportLoaderManager().destroyLoader(GET_DATA_POINT);
        super.onDestroy();
    }

    private void setupRecyclerView() {
        final Context context = recyclerView.getContext();
        final int spacing = getResources().getDimensionPixelOffset(R.dimen.default_spacing_small);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(dailyCardAdapter);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(spacing));
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, final int animationResourceID) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, animationResourceID);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private final LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            WeatherLog.d(TAG, "LoaderJournal", "onCreateLoader id : " + id);
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
                case GET_DATA_POINT:
                    WeatherLog.d(TAG, "LoaderJournal", "GET_DATA_POINT called");
                    WeatherLog.d(TAG, "LoaderJournal",
                            "URI : " + WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME));

                    WeatherLog.d(TAG, "LoaderJournal", "Creating cursorLoader");
                    CursorLoader cursorLoader = new CursorLoader(getApplicationContext(),
                            WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                            new String[]{DataPoint.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.DAILY), null, null);

                    WeatherLog.d(TAG, "LoaderJournal", "Returning cursorLoader");
                    return cursorLoader;
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
            WeatherLog.d(TAG, "LoaderJournal",
                    "onLoadFinished : " + data.getCount() + " - with id : " + loader.getId());
            switch (loader.getId()) {
                case GET_WEATHER_FORECAST:
                    break;
                case GET_DATA_POINT:
                    WeatherLog.d(TAG, "LoaderJournal", "Updating Adapter");
                    dailyCardAdapter.setDailyCursor(data);
                    runLayoutAnimation(recyclerView, R.anim.layout_animation_from_right);
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
                case GET_DATA_POINT:
                    dailyCardAdapter.setDailyCursor(null);
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
                    args.putLong(WeatherForecast.FIELD_PRIMARY_KEY, (long) msg.obj);
                    WeatherLog.d(TAG, "LoaderJournal", "Restarting loader with msg.obj : " + (long) msg.obj);
                    activityWeakReference.get().getSupportLoaderManager().restartLoader(GET_DATA_POINT, args,
                            mainActivity.loaderCallbacks);
                    break;
            }
        }
    }
}