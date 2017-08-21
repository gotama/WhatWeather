package com.gautamastudios.whatweather.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gautamastudios.whatweather.R;
import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.util.GeneralUtils;

public class HorizontalHourlyAdapter extends RecyclerView.Adapter<HorizontalHourlyAdapter.HourlyViewHolder> {

    private static final String TAG = HorizontalHourlyAdapter.class.getSimpleName();

    private Context context;
    private Cursor cursor;
    private boolean validCursor;

    public HorizontalHourlyAdapter(Context context) {
        this.context = context;

        this.validCursor = !(this.cursor == null || this.cursor.isClosed());
    }

    @Override
    public HourlyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_hourly, parent, false);

        if (itemView.getLayoutParams().width == RecyclerView.LayoutParams.MATCH_PARENT) {
            itemView.getLayoutParams().width = RecyclerView.LayoutParams.WRAP_CONTENT;
        }

        return new HourlyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HourlyViewHolder holder, final int position) {
        if (!validCursor) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }

        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        if (cursor.moveToPosition(position)) {
            double doubleTemp = cursor.getDouble(cursor.getColumnIndexOrThrow(DataPoint.FIELD_TEMPERATURE));

            holder.temperature.setText(GeneralUtils.convertDoubleToTemp(doubleTemp));
            String icon = cursor.getString(cursor.getColumnIndex(DataPoint.FIELD_ICON));
            holder.hourlyIcon.setImageDrawable(
                    ContextCompat.getDrawable(context, DataPoint.Icon.getIcon(icon).getResourceId()));
            holder.time.setText(
                    GeneralUtils.convertUnixTimeToTime(cursor.getLong(cursor.getColumnIndex(DataPoint.FIELD_TIME))));

            ViewGroup.LayoutParams params = holder.parentView.getLayoutParams();
            params.height = GeneralUtils.convertDPtoPX(200, context);
            holder.parentView.setLayoutParams(params);

            ViewGroup.LayoutParams overlayParams = holder.overlayView.getLayoutParams();
            overlayParams.height = GeneralUtils.convertDPtoPX((int) Math.round(doubleTemp * 6.33), context);
            holder.overlayView.setLayoutParams(overlayParams);

        }

    }

    public void updateHourlyCursor(Cursor cursor) {
        this.validCursor = cursor != null;
        this.cursor = cursor;

        if (validCursor && cursor.isClosed()) {
            throw new IllegalStateException("couldn't use cursor to position");
        }
    }

    public class HourlyViewHolder extends RecyclerView.ViewHolder {

        private View parentView;
        private View overlayView;
        private LinearLayout temperatureContainer;
        private TextView temperature;
        private RelativeLayout backgroundView;
        private ImageView hourlyIcon;
        private LinearLayout timeContainer;
        private TextView time;

        public HourlyViewHolder(View view) {
            super(view);

            parentView = view;
            overlayView = view.findViewById(R.id.hourly_background);
            temperatureContainer = view.findViewById(R.id.temp_container);
            temperature = view.findViewById(R.id.hourly_temp);
            backgroundView = view.findViewById(R.id.hourly_background_container);
            hourlyIcon = view.findViewById(R.id.hourly_image);
            timeContainer = view.findViewById(R.id.time_container);
            time = view.findViewById(R.id.hourly_time);

        }
    }

    @Override
    public int getItemCount() {
        if (validCursor && cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }
}