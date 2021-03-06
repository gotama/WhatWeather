package com.gautamastudios.whatweather.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.gautamastudios.whatweather.R;
import com.gautamastudios.whatweather.logger.WeatherLog;
import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.ui.animation.ItemOffsetDecoration;
import com.gautamastudios.whatweather.util.GeneralUtils;

import java.util.Locale;

import static com.gautamastudios.whatweather.ui.activity.MainActivity.MSG_CURRENT_CURSOR_CLOSED;
import static com.gautamastudios.whatweather.ui.activity.MainActivity.MSG_DAILY_CURSOR_CLOSED;
import static com.gautamastudios.whatweather.ui.activity.MainActivity.MSG_HOURLY_CURSOR_CLOSED;

public class WeatherViewPageAdapter extends PagerAdapter {

    private static final String TAG = WeatherViewPageAdapter.class.getSimpleName();
    private Context context;
    private Cursor currentCursor;
    private Cursor hourlyCursor;
    private Cursor dailyCursor;

    private Messenger messenger;

    public WeatherViewPageAdapter(Context context, Messenger messenger) {
        this.context = context;
        this.messenger = messenger;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        WeatherPage weatherPage = WeatherPage.values()[position];
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(weatherPage.getLayoutResId(), collection, false);
        layout.setTag(weatherPage.getId());

        RecyclerView recyclerView;

        switch (weatherPage) {
            case TODAY:
                recyclerView = layout.findViewById(R.id.hourly_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(new HorizontalHourlyAdapter(context));

                updateCurrentWeather(layout);
                updateHourlyWeather(recyclerView, true);
                break;
            case TOMORROW:
                recyclerView = layout.findViewById(R.id.tomorrow_hourly_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(new HorizontalHourlyAdapter(context));

                updateTomorrowWeather(layout);
                updateHourlyWeather(recyclerView, true);
                break;
            case DAILY:
                recyclerView = layout.findViewById(R.id.daily_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(new DailyCardAdapter(context));
                final int spacing = context.getResources().getDimensionPixelOffset(R.dimen.default_spacing_small);
                recyclerView.addItemDecoration(new ItemOffsetDecoration(spacing));

                WeatherLog.d(TAG, "CursorJournal", "instantiateItem Daily");
                updateDailyWeather(recyclerView, true);
                break;
        }

        collection.addView(layout);
        return layout;
    }

    private void updateDailyWeather(RecyclerView recyclerView, boolean animate) {
        if (dailyCursor != null && dailyCursor.getCount() > 0) {
            WeatherLog.d(TAG, "CursorJournal", "Daily cursor count : " + dailyCursor.getCount());
            WeatherLog.d(TAG, "CursorJournal", "Daily cursor isClosed : " + dailyCursor.isClosed());
            if (dailyCursor.isClosed()) {
                dailyCursor = null;
//                ((DailyCardAdapter) recyclerView.getAdapter()).updateDailyCursor(null);
                WeatherLog.d(TAG, "CursorJournal", "Sending message");
                sendMessage(MSG_DAILY_CURSOR_CLOSED, null);
            } else if (recyclerView != null){
                ((DailyCardAdapter) recyclerView.getAdapter()).updateDailyCursor(dailyCursor);

                if (animate) {
//                    recyclerView.setLayoutAnimation(
//                            buildAnimation(recyclerView.getContext(), R.anim.layout_animation_from_right));
//                    recyclerView.scheduleLayoutAnimation();
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    private void updateHourlyWeather(RecyclerView recyclerView, boolean animate) {
        if (hourlyCursor != null && hourlyCursor.getCount() > 0) {
            if (hourlyCursor.isClosed()) {
                hourlyCursor = null;
//                ((HorizontalHourlyAdapter) recyclerView.getAdapter()).updateHourlyCursor(null);
                sendMessage(MSG_HOURLY_CURSOR_CLOSED, null);
            } else if (recyclerView != null) {
                ((HorizontalHourlyAdapter) recyclerView.getAdapter()).updateHourlyCursor(hourlyCursor);

                if (animate) {
//                    recyclerView.setLayoutAnimation(
//                            buildAnimation(recyclerView.getContext(), R.anim.layout_animation_from_right));
//                    recyclerView.scheduleLayoutAnimation();
                }

                recyclerView.getAdapter().notifyDataSetChanged();

            }
        }
    }

    private void updateCurrentWeather(ViewGroup layout) {
        if (currentCursor != null && currentCursor.moveToFirst()) {
            if (currentCursor.isClosed()) {
                currentCursor = null;
                sendMessage(MSG_CURRENT_CURSOR_CLOSED, null);
            } else {
                do {
                    TextView currentTime = layout.findViewById(R.id.current_time);
                    TextView currentTemp = layout.findViewById(R.id.current_temp);
                    TextView currentFeels = layout.findViewById(R.id.current_feels_like);
                    TextView currentSummary = layout.findViewById(R.id.current_summary);
                    ImageView currentIcon = layout.findViewById(R.id.current_weather_icon);

                    currentTime.setText(GeneralUtils.convertUnixTimeToDate(
                            currentCursor.getLong(currentCursor.getColumnIndex(DataPoint.FIELD_TIME))));

                    currentTemp.setText(GeneralUtils.convertDoubleToTemp(
                            currentCursor.getDouble(currentCursor.getColumnIndex(DataPoint.FIELD_TEMPERATURE))));

                    String icon = currentCursor.getString(currentCursor.getColumnIndex(DataPoint.FIELD_ICON));
                    currentIcon.setImageDrawable(
                            ContextCompat.getDrawable(context, DataPoint.Icon.getIcon(icon).getResourceId()));

                    currentFeels.setText(String.format(Locale.getDefault(), "Feels like %s", GeneralUtils
                            .convertDoubleToTemp(currentCursor
                                    .getDouble(currentCursor.getColumnIndex(DataPoint.FIELD_APPARENT_TEMPERATURE)))));

                    currentSummary.setText(
                            currentCursor.getString(currentCursor.getColumnIndex(DataPoint.FIELD_SUMMARY)));
                } while (currentCursor.moveToNext());
            }
        }
    }

    private void updateTomorrowWeather(ViewGroup layout) {
        if (dailyCursor != null && dailyCursor.moveToFirst()) {
            if (dailyCursor.isClosed()) {
                dailyCursor = null;
                sendMessage(MSG_CURRENT_CURSOR_CLOSED, null);
            } else {
                TextView currentTime = layout.findViewById(R.id.tomorrow_time);
                TextView currentHiLo = layout.findViewById(R.id.tomorrow_hi_lo);
                TextView currentTemp = layout.findViewById(R.id.tomorrow_temp);
                TextView currentFeels = layout.findViewById(R.id.tomorrow_feels_like);
                TextView currentSummary = layout.findViewById(R.id.tomorrow_summary);
                ImageView currentIcon = layout.findViewById(R.id.tomorrow_weather_icon);

                if (currentTime != null && currentHiLo != null && currentTemp != null && currentFeels != null &&
                        currentSummary != null && currentIcon != null) {
                    if (dailyCursor.moveToNext()) {

                        currentTime.setText(GeneralUtils.convertUnixTimeToDate(
                                dailyCursor.getLong(dailyCursor.getColumnIndex(DataPoint.FIELD_TIME))));

                        double max = dailyCursor.getDouble(dailyCursor.getColumnIndex(DataPoint.FIELD_TEMPERATURE_MAX));
                        double min = dailyCursor.getDouble(dailyCursor.getColumnIndex(DataPoint.FIELD_TEMPERATURE_MIN));

                        currentTemp.setText(GeneralUtils.convertDoubleToTemp((max + min) / 2));

                        String icon = dailyCursor.getString(dailyCursor.getColumnIndex(DataPoint.FIELD_ICON));
                        currentIcon.setImageDrawable(
                                ContextCompat.getDrawable(context, DataPoint.Icon.getIcon(icon).getResourceId()));

                        currentHiLo.setText(String.format(Locale.getDefault(), "Day %1$s - Night %2$s",
                                GeneralUtils.convertDoubleToTemp(max), GeneralUtils.convertDoubleToTemp(min)));

                        currentFeels.setText(String.format(Locale.getDefault(), "Feels like %s",
                                GeneralUtils.convertDoubleToTemp((max + min) / 2)));

                        currentSummary.setText(
                                dailyCursor.getString(dailyCursor.getColumnIndex(DataPoint.FIELD_SUMMARY)));
                    }
                }
            }

        }
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return WeatherPage.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        WeatherPage customPagerEnum = WeatherPage.values()[position];
        return context.getString(customPagerEnum.getTitleResId());
    }

    private WeatherPage currentPage = WeatherPage.TODAY;
    private ViewGroup currentViewGroup;

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        //        if (currentPage.getId() > WeatherPage.getWeatherPage(position).getId()) {
        //            updateCurrentWeather((ViewGroup) object);
        //            updateHourlyWeather((RecyclerView)((ViewGroup) object).findViewWithTag(R.id
        // .hourly_recycler_view), true);
        //        } else {
        //            updateDailyWeather((RecyclerView)((ViewGroup) object).findViewWithTag(R.id.daily_recycler_view));
        //        }

        switch (WeatherPage.getWeatherPage(position)) {
            case TODAY:
                updateCurrentWeather((ViewGroup) object);
                updateHourlyWeather((RecyclerView) ((ViewGroup) object).findViewById(R.id.hourly_recycler_view),
                        false);
                break;
            case TOMORROW:
                updateTomorrowWeather((ViewGroup) object);
                updateHourlyWeather(
                        (RecyclerView) ((ViewGroup) object).findViewById(R.id.tomorrow_hourly_recycler_view), false);
                break;
            case DAILY:
                updateDailyWeather((RecyclerView) ((ViewGroup) object).findViewById(R.id.daily_recycler_view), false);
                break;
        }

        currentPage = WeatherPage.getWeatherPage(position);
        currentViewGroup = (ViewGroup) object;

    }

    public void setCurrentCursor(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            currentCursor = cursor;
            if (currentPage == WeatherPage.TODAY && currentViewGroup != null) {
                updateCurrentWeather(currentViewGroup);
            }
        }
    }

    public void setHourlyCursor(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            hourlyCursor = cursor;
            if (currentViewGroup != null) {
                if (currentPage == WeatherPage.TODAY || currentPage == WeatherPage.TOMORROW) {
                    RecyclerView recyclerView = currentViewGroup.findViewById(R.id.hourly_recycler_view);
                    if (recyclerView != null) {
                        updateHourlyWeather(recyclerView, true);
                    }
                }
            }
        }
    }

    public void setDailyCursor(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            WeatherLog.d(TAG, "CursorJournal", "setting dailyCursor");
            dailyCursor = cursor;

            if (currentViewGroup != null) {
                if (currentPage == WeatherPage.DAILY || currentPage == WeatherPage.TOMORROW) {
                    WeatherLog.d(TAG, "CursorJournal", "setDailyCursor Daily");
                    RecyclerView recyclerView = currentViewGroup.findViewById(R.id.daily_recycler_view);
                    if (recyclerView != null) {
                        updateDailyWeather(recyclerView, true);
                    }

                    updateTomorrowWeather(currentViewGroup);

                    RecyclerView tomorrowRecyclerView = currentViewGroup.findViewById(
                            R.id.tomorrow_hourly_recycler_view);
                    if (recyclerView != null) {
                        updateHourlyWeather(tomorrowRecyclerView, true);
                    }
                }
            }

        }
    }

    private LayoutAnimationController buildAnimation(Context context, int animationResourceID) {
        return AnimationUtils.loadLayoutAnimation(context, animationResourceID);
    }

    public enum WeatherPage {
        TODAY(0, R.string.today_title, R.layout.today_page_layout), TOMORROW(1, R.string.tomorrow_title,
                R.layout.tomorrow_page_layout), DAILY(2, R.string.daily_title, R.layout.daily_page_layout);

        private int id;
        private int titleResourceID;
        private int layoutResourceID;

        WeatherPage(int id, int titleResourceID, int layoutResourceID) {
            this.id = id;
            this.titleResourceID = titleResourceID;
            this.layoutResourceID = layoutResourceID;
        }

        public static WeatherPage getWeatherPage(int id) {
            for (WeatherPage page : values()) {
                if (page.id == id) {
                    return page;
                }
            }

            return null;
        }

        public int getId() {
            return id;
        }

        public int getTitleResId() {
            return titleResourceID;
        }

        public int getLayoutResId() {
            return layoutResourceID;
        }

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

}
