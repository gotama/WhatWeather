package com.gautamastudios.whatweather.ui.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gautamastudios.whatweather.R;
import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.util.GeneralUtils;

public class DailyCardAdapter extends RecyclerView.Adapter<DailyCardAdapter.ViewHolder> {

    private Cursor cursor;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.daily_card, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            holder.dailySummery.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataPoint.FIELD_SUMMARY)));

            String date = GeneralUtils.convertUnixTime(
                    cursor.getLong(cursor.getColumnIndexOrThrow(DataPoint.FIELD_TIME)));
            holder.dailyDate.setText(date);
        }
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dailySummery;
        TextView dailyDate;

        public ViewHolder(View itemView) {
            super(itemView);
            dailySummery = itemView.findViewById(R.id.daily_summary);
            dailyDate = itemView.findViewById(R.id.daily_date);
        }
    }

    public void setDailyCursor(Cursor cursor) {
        this.cursor = cursor;
    }
}
