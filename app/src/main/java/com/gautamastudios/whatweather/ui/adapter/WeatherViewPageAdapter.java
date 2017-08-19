package com.gautamastudios.whatweather.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gautamastudios.whatweather.R;
import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.util.GeneralUtils;

import java.util.Locale;

public class WeatherViewPageAdapter extends PagerAdapter {

    private Context context;
    private Cursor currentCursor;
    private Cursor hourlyCursor;

    public WeatherViewPageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        WeatherPage weatherPage = WeatherPage.values()[position];
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(weatherPage.getLayoutResId(), collection, false);
        layout.setTag(weatherPage.getId());
        updateUI(layout);
        collection.addView(layout);
        return layout;
    }

    private void updateUI(ViewGroup container) {
        View view = container.findViewWithTag(WeatherViewPageAdapter.WeatherPage.TODAY.getId());
        if (view != null) {
            switch (WeatherPage.getWeatherPage((int) view.getTag())) {
                case TODAY:
                    if (currentCursor != null && currentCursor.getCount() > 0) {

                        if (currentCursor.moveToFirst()) {
                            do {
                                TextView currentTime = view.findViewById(R.id.current_time);
                                //TODO hi lo works only off daily
                                TextView currentHiLo = view.findViewById(R.id.current_hi_lo);
                                TextView currentTemp = view.findViewById(R.id.current_temp);
                                TextView currentFeels = view.findViewById(R.id.current_feels_like);
                                TextView currentSummary = view.findViewById(R.id.current_summary);
                                ImageView currentIcon = view.findViewById(R.id.current_weather_icon);

                                currentTime.setText(GeneralUtils.convertUnixTimeToDate(
                                        currentCursor.getLong(currentCursor.getColumnIndex(DataPoint.FIELD_TIME))));

                                currentTemp.setText(GeneralUtils.convertDoubleToTemp(currentCursor
                                        .getDouble(currentCursor.getColumnIndex(DataPoint.FIELD_TEMPERATURE))));

                                String icon = currentCursor.getString(
                                        currentCursor.getColumnIndex(DataPoint.FIELD_ICON));
                                currentIcon.setImageDrawable(ContextCompat
                                        .getDrawable(context, DataPoint.Icon.getIcon(icon).getResourceId()));

                                currentHiLo.setText(String.format(Locale.getDefault(), "Day %1$s - Night %2$s",
                                        GeneralUtils.convertDoubleToTemp(currentCursor.getDouble(currentCursor
                                                .getColumnIndex(DataPoint.FIELD_APPARENT_TEMPERATURE_MAX))),
                                        GeneralUtils.convertDoubleToTemp(currentCursor.getDouble(currentCursor
                                                .getColumnIndex(DataPoint.FIELD_APPARENT_TEMPERATURE_MIN)))));

                                currentFeels.setText(String.format(Locale.getDefault(), "Feels like %s", GeneralUtils
                                        .convertDoubleToTemp(currentCursor.getDouble(
                                                currentCursor.getColumnIndex(DataPoint.FIELD_APPARENT_TEMPERATURE)))));

                                currentSummary.setText(
                                        currentCursor.getString(currentCursor.getColumnIndex(DataPoint.FIELD_SUMMARY)));
                            } while (currentCursor.moveToNext());
                        }

                    }

                    if (hourlyCursor != null && hourlyCursor.getCount() > 0) {

                        RecyclerView recyclerView = container.findViewById(R.id.hourly_recycler_view);
                        if (recyclerView != null) {
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,
                                    LinearLayoutManager.HORIZONTAL, false);

                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(new HorizontalHourlyAdapter(context, hourlyCursor));
                        }
                    }
                    break;

                default:
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

    public void setCurrentCursor(Cursor currentCursor) {
        this.currentCursor = currentCursor;
    }

    public void setHourlyCursor(Cursor hourlyCursor) {
        this.hourlyCursor = hourlyCursor;
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

}
