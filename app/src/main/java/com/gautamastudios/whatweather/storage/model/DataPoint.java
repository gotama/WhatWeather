package com.gautamastudios.whatweather.storage.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;

import com.gautamastudios.whatweather.R;

/**
 * A data point object contains various properties, each representing the average (unless otherwise specified) of a
 * particular weather phenomenon occurring during a period of time: an instant in the case of
 * {@link DataPointType#CURRENTLY},
 * a minute for
 * {@link DataPointType#MINUTELY}, an hour for {@link DataPointType#HOURLY}, and a day for
 * {@link DataPointType#DAILY}. These properties are:
 * <p>
 * {@link DataPoint#apparentTemperature} optional, not on {@link DataPointType#DAILY} <br>
 * The apparent (or “feels like”) temperature in degrees Fahrenheit.
 * <p>
 * {@link DataPoint#apparentTemperatureMax} optional, only on {@link DataPointType#DAILY} <br>
 * The maximum value of {@link DataPoint#apparentTemperature} during a given day.
 * <p>
 * {@link DataPoint#apparentTemperatureMaxTime} optional, only on {@link DataPointType#DAILY} <br>
 * The UNIX time of when {@link DataPoint#apparentTemperatureMax} occurs during a given day.
 * <p>
 * {@link DataPoint#apparentTemperatureMin} optional, only on {@link DataPointType#DAILY} <br>
 * The minimum value of {@link DataPoint#apparentTemperature} during a given day.
 * <p>
 * {@link DataPoint#apparentTemperatureMinTime} optional, only on {@link DataPointType#DAILY} <br>
 * The UNIX time of when {@link DataPoint#apparentTemperatureMin} occurs during a given day.
 * <p>
 * {@link DataPoint#cloudCover} optional <br>
 * The percentage of sky occluded by clouds, between 0 and 1, inclusive.
 * <p>
 * {@link DataPoint#dewPoint} optional <br>
 * The dew point in degrees Fahrenheit.
 * <p>
 * {@link DataPoint#humidity} optional <br>
 * The relative humidity, between 0 and 1, inclusive.
 * <p>
 * {@link DataPoint#icon} optional <br>
 * A machine-readable text summary of this data point, suitable for selecting an icon for display. If defined, this
 * property will have one of the following values: {@link Icon#CLEAR_DAY}, {@link Icon#CLEAR_NIGHT}, {@link Icon#RAIN},
 * {@link Icon#SNOW}, {@link Icon#SLEET}, {@link Icon#WIND}, {@link Icon#FOG}, {@link Icon#CLOUDY},
 * {@link Icon#PARTLY_CLOUDY_DAY}, or {@link Icon#PARTLY_CLOUDY_NIGHT}. <br>(Developers should ensure that a sensible
 * {@link Icon#DEFAULT} is defined, as additional values, such as hail, thunderstorm, or tornado, may be defined in the
 * future.)
 * <p>
 * {@link DataPoint#moonPhase} optional, only on {@link DataPointType#DAILY} <br>
 * The fractional part of the <a href="https://en.wikipedia.org/wiki/New_moon#Lunation_Number">lunation number</a>
 * during the given day: a value of 0 corresponds to a new moon, 0.25 to a first quarter moon, 0.5 to a full moon,
 * and 0.75 to a last quarter moon. (The ranges in between these represent waxing crescent, waxing gibbous, waning
 * gibbous, and waning crescent moons, respectively.)
 * <p>
 * {@link DataPoint#nearestStormBearing} optional, only on {@link DataPointType#CURRENTLY} <br>
 * The approximate direction of the nearest storm in degrees, with true north at 0° and progressing clockwise. (If
 * {@link DataPoint#nearestStormDistance} is zero, then this value will not be defined.)
 * <p>
 * {@link DataPoint#nearestStormDistance} optional, only on {@link DataPointType#CURRENTLY} <br>
 * The approximate distance to the nearest storm in miles. (A storm distance of 0 doesn’t necessarily refer to a
 * storm at the requested location, but rather a storm in the vicinity of that location.)
 * <p>
 * {@link DataPoint#ozone} optional <br>
 * The columnar density of total atmospheric ozone at the given time in Dobson units.
 * <p>
 * {@link DataPoint#precipAccumulation} optional, only on {@link DataPointType#HOURLY} and
 * {@link DataPointType#DAILY} <br>
 * The amount of snowfall accumulation expected to occur, in inches. (If no snowfall is expected, this property will
 * not be defined.)
 * <p>
 * {@link DataPoint#precipIntensity} optional <br>
 * The intensity (in inches of liquid water per hour) of precipitation occurring at the given time. This value is
 * conditional on probability (that is, assuming any precipitation occurs at all) for {@link DataPointType#MINUTELY}
 * data
 * points, and unconditional otherwise.
 * <p>
 * {@link DataPoint#precipIntensityMax} optional, only on {@link DataPointType#DAILY} <br>
 * The maximum value of {@link DataPoint#precipIntensity} during a given day.
 * <p>
 * {@link DataPoint#precipIntensityMaxTime} optional, only on {@link DataPointType#DAILY} <br>
 * The UNIX time of when {@link DataPoint#precipIntensityMax} occurs during a given day.
 * <p>
 * {@link DataPoint#precipProbability} optional <br>
 * The probability of precipitation occurring, between 0 and 1, inclusive.
 * <p>
 * {@link DataPoint#precipType} optional <br>
 * The type of precipitation occurring at the given time. If defined, this property will have one of the following
 * values: {@link Icon#RAIN}, {@link Icon#SNOW}, or {@link Icon#SLEET} (which refers to each of freezing rain, ice
 * pellets, and “wintery mix”). (If {@link DataPoint#precipIntensity} is zero, then this property will not be defined.)
 * <p>
 * {@link DataPoint#pressure} optional <br>
 * The sea-level air pressure in millibars.
 * <p>
 * {@link DataPoint#summary} optional <br>
 * A human-readable text summary of this data point. (This property has millions of possible values, so don’t use it
 * for automated purposes: use the {@link DataPoint#icon} property, instead!)
 * <p>
 * {@link DataPoint#sunriseTime} optional, only on {@link DataPointType#DAILY} <br>
 * The UNIX time of when the sun will rise during a given day.
 * <p>
 * {@link DataPoint#sunsetTime} optional, only on {@link DataPointType#DAILY} <br>
 * The UNIX time of when the sun will set during a given day.
 * <p>
 * {@link DataPoint#temperature} optional, not on {@link DataPointType#MINUTELY} <br>
 * The air temperature in degrees Fahrenheit.
 * <p>
 * {@link DataPoint#temperatureMax} optional, only on {@link DataPointType#DAILY} <br>
 * The maximum value of {@link DataPoint#temperature} during a given day.
 * <p>
 * {@link DataPoint#temperatureMaxTime} optional, only on {@link DataPointType#DAILY} <br>
 * The UNIX time of when {@link DataPoint#temperatureMax} occurs during a given day.
 * <p>
 * {@link DataPoint#temperatureMin} optional, only on {@link DataPointType#DAILY} <br>
 * The minimum value of {@link DataPoint#temperature} during a given day.
 * <p>
 * {@link DataPoint#temperatureMinTime} optional, only on {@link DataPointType#DAILY} <br>
 * The UNIX time of when {@link DataPoint#temperatureMin} occurs during a given day.
 * <p>
 * {@link DataPoint#time} required <br>
 * The UNIX time at which this data point begins. {@link DataPointType#MINUTELY} data point are always aligned to the
 * top of the minute, {@link DataPointType#HOURLY} data point objects to the top of the hour, and
 * {@link DataPointType#DAILY} data point objects to midnight of the day, all according to the local time zone.
 * <p>
 * {@link DataPoint#uvIndex} optional <br>
 * The UV index.
 * <p>
 * {@link DataPoint#uvIndexTime} optional, only on {@link DataPointType#DAILY} <br>
 * The UNIX time of when the maximum {@link DataPoint#uvIndex} occurs during a given day.
 * <p>
 * {@link DataPoint#visibility} optional <br>
 * The average visibility in miles, capped at 10 miles.
 * <p>
 * {@link DataPoint#windBearing} optional <br>
 * The direction that the wind is coming from in degrees, with true north at 0° and progressing clockwise. (If
 * {@link DataPoint#windSpeed} is zero, then this value will not be defined.)
 * <p>
 * {@link DataPoint#windGust} optional <br>
 * The wind gust speed in miles per hour.
 * <p>
 * {@link DataPoint#windGustTime} optional, only on {@link DataPointType#DAILY} <br>
 * The UNIX time of when the maximum {@link DataPoint#windGust} occurs during a given day.
 * <p>
 * {@link DataPoint#windSpeed} optional <br>
 * The wind speed in miles per hour.
 */
@Entity(tableName = DataPoint.TABLE_NAME)
public class DataPoint {

    public static final String TABLE_NAME = "datapoint";
    public static final String FIELD_PRIMARY_KEY = "primary_key";
    public static final String FIELD_APPARENT_TEMPERATURE = "apparent_temperature";
    public static final String FIELD_APPARENT_TEMPERATURE_MAX = "apparent_temperature_max";
    public static final String FIELD_APPARENT_TEMPERATURE_MAX_TIME = "apparent_temperature_max_time";
    public static final String FIELD_APPARENT_TEMPERATURE_MIN = "apparent_temperature_min";
    public static final String FIELD_APPARENT_TEMPERATURE_MIN_TIME = "apparent_temperature_min_time";
    public static final String FIELD_CLOUD_COVER = "cloud_cover";
    public static final String FIELD_DEW_POINT = "dew_point";
    public static final String FIELD_HUMIDITY = "humidity";
    public static final String FIELD_ICON = "icon";
    public static final String FIELD_MOON_PHASE = "moon_phase";
    public static final String FIELD_NEAREST_STORM_BEARING = "nearest_storm_bearing";
    public static final String FIELD_NEAREST_STORM_DISTANCE = "nearest_storm_distance";
    public static final String FIELD_OZONE = "ozone";
    public static final String FIELD_PRECIP_ACCUMULATION = "precip_accumulation";
    public static final String FIELD_PRECIP_INTENSITY = "precip_intensity";
    public static final String FIELD_PRECIP_INTENSITY_MAX = "precip_intensity_max";
    public static final String FIELD_PRECIP_INTENSITY_MAX_TIME = "precip_intensity_max_time";
    public static final String FIELD_PRECIP_PROBABILITY = "precip_probability";
    public static final String FIELD_PRECIP_TYPE = "precip_type";
    public static final String FIELD_PRESSURE = "pressure";
    public static final String FIELD_SUMMARY = "summary";
    public static final String FIELD_SUNRISE_TIME = "sunrise_time";
    public static final String FIELD_SUNSET_TIME = "sunset_time";
    public static final String FIELD_TEMPERATURE = "temperature";
    public static final String FIELD_TEMPERATURE_MAX = "temperature_max";
    public static final String FIELD_TEMPERATURE_MAX_TIME = "temperature_max_time";
    public static final String FIELD_TEMPERATURE_MIN = "temperature_min";
    public static final String FIELD_TEMPERATURE_MIN_TIME = "temperature_min_time";
    public static final String FIELD_TIME = "time";
    public static final String FIELD_UV_INDEX = "uv_index";
    public static final String FIELD_UV_INDEX_TIME = "uv_index_time";
    public static final String FIELD_VISIBILITY = "visibility";
    public static final String FIELD_WIND_BEARING = "wind_bearing";
    public static final String FIELD_WIND_GUST = "wind_gust";
    public static final String FIELD_WIND_GUST_TIME = "wind_gust_time";
    public static final String FIELD_WIND_SPEED = "wind_speed";
    public static final String FIELD_DATA_POINT_TYPE = "data_point_type";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = FIELD_PRIMARY_KEY)
    public long autoGeneratedID;

    @ColumnInfo(name = FIELD_APPARENT_TEMPERATURE)
    public double apparentTemperature;

    @ColumnInfo(name = FIELD_APPARENT_TEMPERATURE_MAX)
    public double apparentTemperatureMax;

    @ColumnInfo(name = FIELD_APPARENT_TEMPERATURE_MAX_TIME)
    public long apparentTemperatureMaxTime;

    @ColumnInfo(name = FIELD_APPARENT_TEMPERATURE_MIN)
    public double apparentTemperatureMin;

    @ColumnInfo(name = FIELD_APPARENT_TEMPERATURE_MIN_TIME)
    public long apparentTemperatureMinTime;

    @ColumnInfo(name = FIELD_CLOUD_COVER)
    public double cloudCover;

    @ColumnInfo(name = FIELD_DEW_POINT)
    public double dewPoint;

    @ColumnInfo(name = FIELD_HUMIDITY)
    public double humidity;

    @ColumnInfo(name = FIELD_ICON)
    public String icon;

    @ColumnInfo(name = FIELD_MOON_PHASE)
    public double moonPhase;

    @ColumnInfo(name = FIELD_NEAREST_STORM_BEARING)
    public int nearestStormBearing;

    @ColumnInfo(name = FIELD_NEAREST_STORM_DISTANCE)
    public int nearestStormDistance;

    @ColumnInfo(name = FIELD_OZONE)
    public double ozone;

    @ColumnInfo(name = FIELD_PRECIP_ACCUMULATION)
    public double precipAccumulation;

    @ColumnInfo(name = FIELD_PRECIP_INTENSITY)
    public double precipIntensity;

    @ColumnInfo(name = FIELD_PRECIP_INTENSITY_MAX)
    public double precipIntensityMax;

    @ColumnInfo(name = FIELD_PRECIP_INTENSITY_MAX_TIME)
    public long precipIntensityMaxTime;

    @ColumnInfo(name = FIELD_PRECIP_PROBABILITY)
    public double precipProbability;

    @ColumnInfo(name = FIELD_PRECIP_TYPE)
    public String precipType;

    @ColumnInfo(name = FIELD_PRESSURE)
    public double pressure;

    @ColumnInfo(name = FIELD_SUMMARY)
    public String summary;

    @ColumnInfo(name = FIELD_SUNRISE_TIME)
    public long sunriseTime;

    @ColumnInfo(name = FIELD_SUNSET_TIME)
    public long sunsetTime;

    @ColumnInfo(name = FIELD_TEMPERATURE)
    public double temperature;

    @ColumnInfo(name = FIELD_TEMPERATURE_MAX)
    public double temperatureMax;

    @ColumnInfo(name = FIELD_TEMPERATURE_MAX_TIME)
    public long temperatureMaxTime;

    @ColumnInfo(name = FIELD_TEMPERATURE_MIN)
    public double temperatureMin;

    @ColumnInfo(name = FIELD_TEMPERATURE_MIN_TIME)
    public long temperatureMinTime;

    @ColumnInfo(name = FIELD_TIME)
    public long time;

    @ColumnInfo(name = FIELD_UV_INDEX)
    public int uvIndex;

    @ColumnInfo(name = FIELD_UV_INDEX_TIME)
    public long uvIndexTime;

    @ColumnInfo(name = FIELD_VISIBILITY)
    public double visibility;

    @ColumnInfo(name = FIELD_WIND_BEARING)
    public int windBearing;

    @ColumnInfo(name = FIELD_WIND_GUST)
    public double windGust;

    @ColumnInfo(name = FIELD_WIND_GUST_TIME)
    public long windGustTime;

    @ColumnInfo(name = FIELD_WIND_SPEED)
    public double windSpeed;

    @ColumnInfo(name = FIELD_DATA_POINT_TYPE)
    public int type;

    public void setDataPointType(@DataPointType int type) {
        this.type = type;
    }

    /**
     * Create a new {@link DataPoint} from the specified {@link ContentValues}.
     *
     * @param values A {@link ContentValues} that contains
     *               {@link #FIELD_PRIMARY_KEY}, {@link #FIELD_APPARENT_TEMPERATURE},
     *               {@link #FIELD_APPARENT_TEMPERATURE_MAX}, {@link #FIELD_APPARENT_TEMPERATURE_MAX_TIME},
     *               {@link #FIELD_APPARENT_TEMPERATURE_MIN}, {@link #FIELD_APPARENT_TEMPERATURE_MIN_TIME},
     *               {@link #FIELD_CLOUD_COVER}, {@link #FIELD_DEW_POINT}, {@link #FIELD_HUMIDITY}, {@link #FIELD_ICON},
     *               {@link #FIELD_MOON_PHASE}, {@link #FIELD_NEAREST_STORM_BEARING},
     *               {@link #FIELD_NEAREST_STORM_DISTANCE},
     *               {@link #FIELD_OZONE}, {@link #FIELD_PRECIP_ACCUMULATION}, {@link #FIELD_PRECIP_INTENSITY},
     *               {@link #FIELD_PRECIP_INTENSITY_MAX}, {@link #FIELD_PRECIP_INTENSITY_MAX_TIME},
     *               {@link #FIELD_PRECIP_PROBABILITY},
     *               {@link #FIELD_PRECIP_TYPE}, {@link #FIELD_PRESSURE}, {@link #FIELD_SUMMARY},
     *               {@link #FIELD_SUNRISE_TIME},
     *               {@link #FIELD_SUNSET_TIME}, {@link #FIELD_TEMPERATURE}, {@link #FIELD_TEMPERATURE_MAX},
     *               {@link #FIELD_TEMPERATURE_MAX_TIME}, {@link #FIELD_TEMPERATURE_MIN},
     *               {@link #FIELD_TEMPERATURE_MIN_TIME},
     *               {@link #FIELD_TIME}, {@link #FIELD_UV_INDEX}, {@link #FIELD_UV_INDEX_TIME},
     *               {@link #FIELD_VISIBILITY},
     *               {@link #FIELD_WIND_BEARING}, {@link #FIELD_WIND_GUST}, {@link #FIELD_WIND_GUST_TIME},
     *               {@link #FIELD_WIND_SPEED}, {@link #FIELD_DATA_POINT_TYPE}
     * @return An instance of {@link DataPoint}.
     */
    public static DataPoint fromContentValues(ContentValues values) {
        final DataPoint dataPoint = new DataPoint();
        if (values.containsKey(FIELD_PRIMARY_KEY)) {
            dataPoint.autoGeneratedID = values.getAsLong(FIELD_PRIMARY_KEY);
        }
        if (values.containsKey(FIELD_APPARENT_TEMPERATURE)) {
            dataPoint.apparentTemperature = values.getAsDouble(FIELD_APPARENT_TEMPERATURE);
        }
        if (values.containsKey(FIELD_APPARENT_TEMPERATURE_MAX)) {
            dataPoint.apparentTemperatureMax = values.getAsDouble(FIELD_APPARENT_TEMPERATURE_MAX);
        }
        if (values.containsKey(FIELD_APPARENT_TEMPERATURE_MAX_TIME)) {
            dataPoint.apparentTemperatureMaxTime = values.getAsLong(FIELD_APPARENT_TEMPERATURE_MAX_TIME);
        }
        if (values.containsKey(FIELD_APPARENT_TEMPERATURE_MIN)) {
            dataPoint.apparentTemperatureMin = values.getAsDouble(FIELD_APPARENT_TEMPERATURE_MIN);
        }
        if (values.containsKey(FIELD_APPARENT_TEMPERATURE_MIN_TIME)) {
            dataPoint.apparentTemperatureMinTime = values.getAsLong(FIELD_APPARENT_TEMPERATURE_MIN_TIME);
        }
        if (values.containsKey(FIELD_CLOUD_COVER)) {
            dataPoint.cloudCover = values.getAsDouble(FIELD_CLOUD_COVER);
        }
        if (values.containsKey(FIELD_DEW_POINT)) {
            dataPoint.dewPoint = values.getAsDouble(FIELD_DEW_POINT);
        }
        if (values.containsKey(FIELD_HUMIDITY)) {
            dataPoint.humidity = values.getAsDouble(FIELD_HUMIDITY);
        }
        if (values.containsKey(FIELD_ICON)) {
            dataPoint.icon = values.getAsString(FIELD_ICON);
        }
        if (values.containsKey(FIELD_MOON_PHASE)) {
            dataPoint.moonPhase = values.getAsDouble(FIELD_MOON_PHASE);
        }
        if (values.containsKey(FIELD_NEAREST_STORM_BEARING)) {
            dataPoint.nearestStormBearing = values.getAsInteger(FIELD_NEAREST_STORM_BEARING);
        }
        if (values.containsKey(FIELD_NEAREST_STORM_DISTANCE)) {
            dataPoint.nearestStormDistance = values.getAsInteger(FIELD_NEAREST_STORM_DISTANCE);
        }
        if (values.containsKey(FIELD_OZONE)) {
            dataPoint.ozone = values.getAsDouble(FIELD_OZONE);
        }
        if (values.containsKey(FIELD_PRECIP_ACCUMULATION)) {
            dataPoint.precipAccumulation = values.getAsDouble(FIELD_PRECIP_ACCUMULATION);
        }
        if (values.containsKey(FIELD_PRECIP_INTENSITY)) {
            dataPoint.precipIntensity = values.getAsDouble(FIELD_PRECIP_INTENSITY);
        }
        if (values.containsKey(FIELD_PRECIP_INTENSITY_MAX)) {
            dataPoint.precipIntensityMax = values.getAsDouble(FIELD_PRECIP_INTENSITY_MAX);
        }
        if (values.containsKey(FIELD_PRECIP_INTENSITY_MAX_TIME)) {
            dataPoint.precipIntensityMaxTime = values.getAsLong(FIELD_PRECIP_INTENSITY_MAX_TIME);
        }
        if (values.containsKey(FIELD_PRECIP_PROBABILITY)) {
            dataPoint.precipProbability = values.getAsDouble(FIELD_PRECIP_PROBABILITY);
        }
        if (values.containsKey(FIELD_PRECIP_TYPE)) {
            dataPoint.precipType = values.getAsString(FIELD_PRECIP_TYPE);
        }
        if (values.containsKey(FIELD_PRESSURE)) {
            dataPoint.pressure = values.getAsDouble(FIELD_PRESSURE);
        }
        if (values.containsKey(FIELD_SUMMARY)) {
            dataPoint.summary = values.getAsString(FIELD_SUMMARY);
        }
        if (values.containsKey(FIELD_SUNRISE_TIME)) {
            dataPoint.sunriseTime = values.getAsLong(FIELD_SUNRISE_TIME);
        }
        if (values.containsKey(FIELD_SUNSET_TIME)) {
            dataPoint.sunsetTime = values.getAsLong(FIELD_SUNSET_TIME);
        }
        if (values.containsKey(FIELD_TEMPERATURE)) {
            dataPoint.temperature = values.getAsDouble(FIELD_TEMPERATURE);
        }
        if (values.containsKey(FIELD_TEMPERATURE_MAX)) {
            dataPoint.temperatureMax = values.getAsDouble(FIELD_TEMPERATURE_MAX);
        }
        if (values.containsKey(FIELD_TEMPERATURE_MAX_TIME)) {
            dataPoint.temperatureMaxTime = values.getAsLong(FIELD_TEMPERATURE_MAX_TIME);
        }
        if (values.containsKey(FIELD_TEMPERATURE_MIN)) {
            dataPoint.temperatureMin = values.getAsDouble(FIELD_TEMPERATURE_MIN);
        }
        if (values.containsKey(FIELD_TEMPERATURE_MIN_TIME)) {
            dataPoint.temperatureMinTime = values.getAsLong(FIELD_TEMPERATURE_MIN_TIME);
        }
        if (values.containsKey(FIELD_TIME)) {
            dataPoint.time = values.getAsLong(FIELD_TIME);
        }
        if (values.containsKey(FIELD_UV_INDEX)) {
            dataPoint.uvIndex = values.getAsInteger(FIELD_UV_INDEX);
        }
        if (values.containsKey(FIELD_UV_INDEX_TIME)) {
            dataPoint.uvIndexTime = values.getAsLong(FIELD_UV_INDEX_TIME);
        }
        if (values.containsKey(FIELD_VISIBILITY)) {
            dataPoint.visibility = values.getAsDouble(FIELD_VISIBILITY);
        }
        if (values.containsKey(FIELD_WIND_BEARING)) {
            dataPoint.windBearing = values.getAsInteger(FIELD_WIND_BEARING);
        }
        if (values.containsKey(FIELD_WIND_GUST)) {
            dataPoint.windGust = values.getAsDouble(FIELD_WIND_GUST);
        }
        if (values.containsKey(FIELD_WIND_GUST_TIME)) {
            dataPoint.windGustTime = values.getAsLong(FIELD_WIND_GUST_TIME);
        }
        if (values.containsKey(FIELD_WIND_SPEED)) {
            dataPoint.windSpeed = values.getAsDouble(FIELD_WIND_SPEED);
        }
        if (values.containsKey(FIELD_DATA_POINT_TYPE)) {
            dataPoint.type = values.getAsInteger(FIELD_DATA_POINT_TYPE);
        }
        return dataPoint;
    }

    public enum Icon {
        CLEAR_DAY("clear-day", R.drawable.ic_sunny),
        CLEAR_NIGHT("clear-night", R.drawable.ic_moon),
        RAIN("rain", R.drawable.ic_rain_windy),
        SNOW("snow", R.drawable.ic_snowflake),
        SLEET("sleet", R.drawable.ic_sleet),
        WIND("wind", R.drawable.ic_sunny_windy),
        FOG("fog", R.drawable.ic_mist),
        CLOUDY("cloudy", R.drawable.ic_partly_cloudy),
        PARTLY_CLOUDY_DAY("partly-cloudy-day", R.drawable.ic_partly_cloudy),
        PARTLY_CLOUDY_NIGHT("partly-cloudy-night", R.drawable.ic_partly_cloudy),
        HAIL("hail", R.drawable.ic_hail),
        THUNDERSTORM("thunderstorm", R.drawable.ic_rain_thunder_warning),
        DEFAULT("", R.drawable.ic_sleet);

        private String displayName;
        private int resourceId;

        Icon(String displayName, int resourceId) {
            this.displayName = displayName;
            this.resourceId = resourceId;
        }

        public static Icon getIcon(String iconValue) {
            for (Icon icon : values()) {
                if (iconValue.equals(icon.getDisplayName())) {
                    return icon;
                }
            }
            return DEFAULT;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getResourceId() {
            return resourceId;
        }
    }
}
