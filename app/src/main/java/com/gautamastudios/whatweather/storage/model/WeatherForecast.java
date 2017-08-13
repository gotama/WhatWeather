package com.gautamastudios.whatweather.storage.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * API responses consist of a UTF-8-encoded, JSON-formatted object with the following properties:
 * <p>
 * {@link WeatherForecast#latitude} required <br>
 * The requested latitude.
 * <p>
 * {@link WeatherForecast#longitude} required <br>
 * The requested longitude.
 * <p>
 * {@link WeatherForecast#timezone} (e.g. America/New_York) required <br>
 * The IANA timezone name for the requested location. This is used for text summaries and for determining when
 * {@link WeatherForecast#hourly} and {@link WeatherForecast#daily} data block objects begin.
 * <p>
 * {@link WeatherForecast#offset} deprecated <br>
 * The current timezone offset in hours. (Use of this property will almost certainly result in Daylight Saving Time
 * bugs. Please use {@link WeatherForecast#timezone}, instead.)
 * <p>
 * {@link WeatherForecast#currently} optional <br>
 * A {@link DataPoint} containing the current weather conditions at the requested location.
 * <p>
 * {@link WeatherForecast#minutely} optional <br>
 * A {@link DataBlock} containing the weather conditions minute-by-minute for the next hour.
 * <p>
 * {@link WeatherForecast#hourly} optional <br>
 * A {@link DataBlock} containing the weather conditions hour-by-hour for the next two days.
 * <p>
 * {@link WeatherForecast#daily} optional <br>
 * A {@link DataBlock} containing the weather conditions day-by-day for the next week.
 * <p>
 * {@link WeatherForecast#alerts} optional <br>
 * An {@link Alert} array, which, if present, contains any severe weather alerts pertinent to the requested location.
 * <p>
 * {@link WeatherForecast#flags} optional <br>
 * A {@link Flags} object containing miscellaneous metadata about the request.
 */

@Entity(tableName = WeatherForecast.TABLE_NAME,
        indices = {@Index(value = {WeatherForecast.FIELD_LATITUDE, WeatherForecast.FIELD_LONGITUDE}, unique = true)})
public class WeatherForecast {

    public static final String TABLE_NAME = "weather";
    public static final String FIELD_PRIMARY_KEY = "primary_key";
    public static final String FIELD_LATITUDE = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";
    public static final String FIELD_TIMEZONE = "timezone";
    public static final String FIELD_OFFSET = "offset";

    @PrimaryKey
    @ColumnInfo(name = FIELD_PRIMARY_KEY)
    public long timeStampPrimaryKey;

    @ColumnInfo(name = FIELD_LATITUDE)
    public double latitude;

    @ColumnInfo(name = FIELD_LONGITUDE)
    public double longitude;

    @ColumnInfo(name = FIELD_TIMEZONE)
    public String timezone;

    @ColumnInfo(name = FIELD_OFFSET)
    public String offset;

    @Ignore
    private DataPoint currently;

    @Ignore
    private DataBlock minutely;

    @Ignore
    private DataBlock hourly;

    @Ignore
    private DataBlock daily;

    @Ignore
    private List<Alert> alerts;

    @Ignore
    private Flags flags;

    /**
     * Create a new {@link WeatherForecast} from the specified {@link ContentValues}.
     *
     * @param values A {@link ContentValues} that contains {@link #FIELD_PRIMARY_KEY}, {@link #FIELD_LATITUDE},
     *               {@link #FIELD_LONGITUDE}, {@link #FIELD_TIMEZONE}, {@link #FIELD_OFFSET}
     * @return An instance of {@link WeatherForecast}.
     */
    public static WeatherForecast fromContentValues(ContentValues values) {
        final WeatherForecast weatherForecast = new WeatherForecast();
        if (values.containsKey(FIELD_PRIMARY_KEY)) {
            weatherForecast.timeStampPrimaryKey = values.getAsLong(FIELD_PRIMARY_KEY);
        }
        if (values.containsKey(FIELD_LATITUDE)) {
            weatherForecast.latitude = values.getAsDouble(FIELD_LATITUDE);
        }
        if (values.containsKey(FIELD_LONGITUDE)) {
            weatherForecast.longitude = values.getAsDouble(FIELD_LONGITUDE);
        }
        if (values.containsKey(FIELD_TIMEZONE)) {
            weatherForecast.timezone = values.getAsString(FIELD_TIMEZONE);
        }
        if (values.containsKey(FIELD_OFFSET)) {
            weatherForecast.offset = values.getAsString(FIELD_OFFSET);
        }
        return weatherForecast;
    }

    public static WeatherForecast buildWeatherForecastFromResponse(String weatherJsonRespone) {
        WeatherForecast weatherForecast = new Gson().fromJson(weatherJsonRespone, WeatherForecast.class);
        weatherForecast.timeStampPrimaryKey = weatherForecast.getCurrently().time;
        weatherForecast.currently.setDataPointType(DataPointType.CURRENTLY);

        weatherForecast.getMinutely().setDataBlockType(DataPointType.MINUTELY);
        weatherForecast.getHourly().setDataBlockType(DataPointType.HOURLY);
        weatherForecast.getDaily().setDataBlockType(DataPointType.DAILY);

        return weatherForecast;
    }

    public DataPoint getCurrently() {
        return currently;
    }

    public DataBlock getMinutely() {
        if (minutely == null) {
            minutely = new DataBlock();
        }
        return minutely;
    }

    public DataBlock getHourly() {
        if (hourly == null) {
            hourly = new DataBlock();
        }
        return hourly;
    }

    public DataBlock getDaily() {
        if (daily == null) {
            daily = new DataBlock();
        }
        return daily;
    }

    public List<Alert> getAlerts() {
        if (alerts == null) {
            alerts = new ArrayList<>();
        }
        return alerts;
    }

    public Flags getFlags() {
        if (flags == null) {
            flags = new Flags();
        }
        return flags;
    }

    public void setCurrently(DataPoint currently) {
        this.currently = currently;
    }

    public void setMinutely(DataBlock minutely) {
        this.minutely = minutely;
    }

    public void setHourly(DataBlock hourly) {
        this.hourly = hourly;
    }

    public void setDaily(DataBlock daily) {
        this.daily = daily;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }
}
