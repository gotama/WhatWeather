package com.gautamastudios.whatweather.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gautamastudios.whatweather.R;
import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.util.GeneralUtils;

public class DailyCardAdapter extends RecyclerView.Adapter<DailyCardAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;
    private boolean validCursor;

    public DailyCardAdapter(Context context) {
        this.context = context;
        this.validCursor = !(this.cursor == null || this.cursor.isClosed());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.daily_card, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!validCursor) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }

        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        if (cursor.moveToPosition(position)) {
            holder.dailySummery.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataPoint.FIELD_SUMMARY)));

            holder.dailyDate.setText(GeneralUtils.convertUnixTimeToDate(
                    cursor.getLong(cursor.getColumnIndex(DataPoint.FIELD_TIME))));

            double precip = cursor.getDouble(cursor.getColumnIndex(DataPoint.FIELD_PRECIP_PROBABILITY));
            double percentage = ((precip - 0) * 100) / (1 - 0);
            holder.dailyPercip.setText(String.valueOf(percentage) + "%");

            String icon = cursor.getString(cursor.getColumnIndex(DataPoint.FIELD_ICON));
            holder.dailyIcon.setImageDrawable(
                    ContextCompat.getDrawable(context, DataPoint.Icon.getIcon(icon).getResourceId()));

            holder.dailyHigh.setText(GeneralUtils.convertDoubleToTemp(
                    cursor.getDouble(cursor.getColumnIndex(DataPoint.FIELD_TEMPERATURE_MAX))));

            holder.dailyLow.setText(GeneralUtils.convertDoubleToTemp(
                    cursor.getDouble(cursor.getColumnIndex(DataPoint.FIELD_TEMPERATURE_MIN))));
        }
    }

    @Override
    public int getItemCount() {
        if (validCursor && cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    public void updateDailyCursor(Cursor cursor) {
        this.validCursor = cursor != null;
        this.cursor = cursor;

        if (validCursor && cursor.isClosed()) {
            throw new IllegalStateException("couldn't use cursor to position");
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dailySummery;
        TextView dailyDate;
        TextView dailyPercip;
        ImageView dailyIcon;
        TextView dailyHigh;
        TextView dailyLow;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    //TODO load fragment
                }
            });

            dailySummery = itemView.findViewById(R.id.daily_summary);
            dailyDate = itemView.findViewById(R.id.daily_date);
            dailyPercip = itemView.findViewById(R.id.daily_percip_percentage);
            dailyIcon = itemView.findViewById(R.id.daily_icon);
            dailyHigh = itemView.findViewById(R.id.daily_high_temp);
            dailyLow = itemView.findViewById(R.id.daily_low_temp);
        }
    }
}