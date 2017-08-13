package com.gautamastudios.whatweather;

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
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gautamastudios.whatweather.service.ForecastSyncJobService;
import com.gautamastudios.whatweather.service.NetworkType;
import com.gautamastudios.whatweather.storage.model.WeatherForecast;
import com.gautamastudios.whatweather.storage.provider.WeatherForecastProvider;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final int GET_WEATHER_FORECAST_CACHE = 1;
    public static final int MSG_JOB_STOP = 0;

    public static final String MESSENGER_INTENT_KEY = BuildConfig.APPLICATION_ID + ".MESSENGER_INTENT_KEY";

    private WeatherForecastAdapter weatherForecastAdapter;
    private IncomingMessageHandler incomingMessageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        incomingMessageHandler = new IncomingMessageHandler(this);

        final RecyclerView list = findViewById(R.id.result_list);
        list.setLayoutManager(new LinearLayoutManager(list.getContext()));
        weatherForecastAdapter = new WeatherForecastAdapter();
        list.setAdapter(weatherForecastAdapter);

        //Fetch cached forecast
        getSupportLoaderManager().initLoader(GET_WEATHER_FORECAST_CACHE, null, loaderCallbacks);
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
        WeatherApplication.getInstance().scheduleJob(new ComponentName(this, ForecastSyncJobService.class), 0, 10000,
                NetworkType.NETWORK_TYPE_ANY, false, false, new PersistableBundle());
    }

    private final LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case GET_WEATHER_FORECAST_CACHE:
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
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            switch (loader.getId()) {
                case GET_WEATHER_FORECAST_CACHE:
                    weatherForecastAdapter.setWeatherForecast(data);
                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            switch (loader.getId()) {
                case GET_WEATHER_FORECAST_CACHE:
                    weatherForecastAdapter.setWeatherForecast(null);
                    break;
            }
        }

    };

    /**
     * A {@link IncomingMessageHandler} allows you to send messages associated with a thread. A {@link Messenger}
     * uses this handler to communicate from {@link ForecastSyncJobService}.
     */
    private static class IncomingMessageHandler extends Handler {

        // Prevent possible leaks with a weak reference.
        private WeakReference<MainActivity> activityWeakReference;

        IncomingMessageHandler(MainActivity activity) {
            super(/* default looper */);
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = activityWeakReference.get();
            if (mainActivity == null) {
                // Activity is no longer available, exit.
                return;
            }
            switch (msg.what) {
                /*
                 * Receives callback from the service when a job has landed
                 * on the app. Turns on indicator and sends a message to turn it off after
                 * a second.
                 */
                case MSG_JOB_STOP:
                    Bundle args = new Bundle();
                    args.putLong(WeatherForecast.FIELD_PRIMARY_KEY, (long) msg.obj);
                    activityWeakReference.get().getSupportLoaderManager().initLoader(GET_WEATHER_FORECAST_CACHE, args,
                            mainActivity.loaderCallbacks);
                    break;
            }
        }
    }

    private static class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ViewHolder> {

        private Cursor cursor;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (cursor.moveToPosition(position)) {
                holder.textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(WeatherForecast.FIELD_LATITUDE)));
            }
        }

        @Override
        public int getItemCount() {
            return cursor == null ? 0 : cursor.getCount();
        }

        void setWeatherForecast(Cursor cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            ViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext())
                        .inflate(android.R.layout.simple_list_item_1, parent, false));
                textView = itemView.findViewById(android.R.id.text1);
            }

        }

    }
}
