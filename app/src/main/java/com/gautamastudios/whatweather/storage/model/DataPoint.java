package com.gautamastudios.whatweather.storage.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

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
@Entity(tableName = "datapoint")
public class DataPoint {

    public static final String FIELD_DATA_POINT_TYPE = "data_point_type";

    @PrimaryKey(autoGenerate = true)
    private int autoGeneratedID;

    @ColumnInfo(name = "apparent_temperature")
    private double apparentTemperature;

    @ColumnInfo(name = "apparent_temperature_max")
    private double apparentTemperatureMax;

    @ColumnInfo(name = "apparent_temperature_max_time")
    private long apparentTemperatureMaxTime;

    @ColumnInfo(name = "apparent_temperature_min")
    private double apparentTemperatureMin;

    @ColumnInfo(name = "apparent_temperature_min_time")
    private long apparentTemperatureMinTime;

    @ColumnInfo(name = "cloud_cover")
    private double cloudCover;

    @ColumnInfo(name = "dew_point")
    private double dewPoint;

    @ColumnInfo(name = "humidity")
    private double humidity;

    @ColumnInfo(name = "icon")
    private String icon;

    @ColumnInfo(name = "moon_phase")
    private double moonPhase;

    @ColumnInfo(name = "nearest_storm_bearing")
    private int nearestStormBearing;

    @ColumnInfo(name = "nearest_storm_distance")
    private int nearestStormDistance;

    @ColumnInfo(name = "ozone")
    private double ozone;

    @ColumnInfo(name = "precip_accumulation")
    private double precipAccumulation;

    @ColumnInfo(name = "precip_intensity")
    private double precipIntensity;

    @ColumnInfo(name = "precip_intensity_max")
    private double precipIntensityMax;

    @ColumnInfo(name = "precip_intensity_max_time")
    private long precipIntensityMaxTime;

    @ColumnInfo(name = "precip_probability")
    private double precipProbability;

    @ColumnInfo(name = "precip_type")
    private String precipType;

    @ColumnInfo(name = "pressure")
    private double pressure;

    @ColumnInfo(name = "summary")
    private String summary;

    @ColumnInfo(name = "sunrise_time")
    private long sunriseTime;

    @ColumnInfo(name = "sunset_time")
    private long sunsetTime;

    @ColumnInfo(name = "temperature")
    private double temperature;

    @ColumnInfo(name = "temperature_max")
    private double temperatureMax;

    @ColumnInfo(name = "temperature_max_time")
    private long temperatureMaxTime;

    @ColumnInfo(name = "temperature_min")
    private double temperatureMin;

    @ColumnInfo(name = "temperature_min_time")
    private long temperatureMinTime;

    @ColumnInfo(name = "time")
    private long time;

    @ColumnInfo(name = "uv_index")
    private int uvIndex;

    @ColumnInfo(name = "uv_index_time")
    private long uvIndexTime;

    @ColumnInfo(name = "visibility")
    private double visibility;

    @ColumnInfo(name = "wind_bearing")
    private int windBearing;

    @ColumnInfo(name = "wind_gust")
    private double windGust;

    @ColumnInfo(name = "wind_gust_time")
    private long windGustTime;

    @ColumnInfo(name = "wind_speed")
    private double windSpeed;

    @ColumnInfo(name = FIELD_DATA_POINT_TYPE)
    private int type;

    public void setDataPointType(@DataPointType int type) {
        this.type = type;
    }

    // TODO DEFAULT("severe-weather") when UI layer is being built
    // (Developers should ensure that a sensible default is defined, as
    // * additional values, such as hail, thunderstorm, or tornado, may be defined in the future.)
    private enum Icon {
        CLEAR_DAY("clear-day"), CLEAR_NIGHT("clear-night"), RAIN("rain"), SNOW("snow"), SLEET("sleet"), WIND("wind"),
        FOG("fog"), CLOUDY("cloudy"), PARTLY_CLOUDY_DAY("partly-cloudy-day"), PARTLY_CLOUDY_NIGHT(
                "partly-cloudy-night"), DEFAULT("severe-weather");

        private String displayName;

        Icon(String displayName) {
            this.displayName = displayName;
        }
    }

    public DataPoint(int autoGeneratedID, double apparentTemperature, double apparentTemperatureMax,
            long apparentTemperatureMaxTime, double apparentTemperatureMin, long apparentTemperatureMinTime,
            double cloudCover, double dewPoint, double humidity, String icon, double moonPhase, int nearestStormBearing,
            int nearestStormDistance, double ozone, double precipAccumulation, double precipIntensity,
            double precipIntensityMax, long precipIntensityMaxTime, double precipProbability, String precipType,
            double pressure, String summary, long sunriseTime, long sunsetTime, double temperature,
            double temperatureMax, long temperatureMaxTime, double temperatureMin, long temperatureMinTime, long time,
            int uvIndex, long uvIndexTime, double visibility, int windBearing, double windGust, long windGustTime,
            double windSpeed, int type) {
        this.autoGeneratedID = autoGeneratedID;
        this.apparentTemperature = apparentTemperature;
        this.apparentTemperatureMax = apparentTemperatureMax;
        this.apparentTemperatureMaxTime = apparentTemperatureMaxTime;
        this.apparentTemperatureMin = apparentTemperatureMin;
        this.apparentTemperatureMinTime = apparentTemperatureMinTime;
        this.cloudCover = cloudCover;
        this.dewPoint = dewPoint;
        this.humidity = humidity;
        this.icon = icon;
        this.moonPhase = moonPhase;
        this.nearestStormBearing = nearestStormBearing;
        this.nearestStormDistance = nearestStormDistance;
        this.ozone = ozone;
        this.precipAccumulation = precipAccumulation;
        this.precipIntensity = precipIntensity;
        this.precipIntensityMax = precipIntensityMax;
        this.precipIntensityMaxTime = precipIntensityMaxTime;
        this.precipProbability = precipProbability;
        this.precipType = precipType;
        this.pressure = pressure;
        this.summary = summary;
        this.sunriseTime = sunriseTime;
        this.sunsetTime = sunsetTime;
        this.temperature = temperature;
        this.temperatureMax = temperatureMax;
        this.temperatureMaxTime = temperatureMaxTime;
        this.temperatureMin = temperatureMin;
        this.temperatureMinTime = temperatureMinTime;
        this.time = time;
        this.uvIndex = uvIndex;
        this.uvIndexTime = uvIndexTime;
        this.visibility = visibility;
        this.windBearing = windBearing;
        this.windGust = windGust;
        this.windGustTime = windGustTime;
        this.windSpeed = windSpeed;
        this.type = type;
    }

    public int getAutoGeneratedID() {
        return autoGeneratedID;
    }

    public double getApparentTemperature() {
        return apparentTemperature;
    }

    public double getApparentTemperatureMax() {
        return apparentTemperatureMax;
    }

    public long getApparentTemperatureMaxTime() {
        return apparentTemperatureMaxTime;
    }

    public double getApparentTemperatureMin() {
        return apparentTemperatureMin;
    }

    public long getApparentTemperatureMinTime() {
        return apparentTemperatureMinTime;
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public double getDewPoint() {
        return dewPoint;
    }

    public double getHumidity() {
        return humidity;
    }

    public String getIcon() {
        return icon;
    }

    public double getMoonPhase() {
        return moonPhase;
    }

    public int getNearestStormBearing() {
        return nearestStormBearing;
    }

    public int getNearestStormDistance() {
        return nearestStormDistance;
    }

    public double getOzone() {
        return ozone;
    }

    public double getPrecipAccumulation() {
        return precipAccumulation;
    }

    public double getPrecipIntensity() {
        return precipIntensity;
    }

    public double getPrecipIntensityMax() {
        return precipIntensityMax;
    }

    public long getPrecipIntensityMaxTime() {
        return precipIntensityMaxTime;
    }

    public double getPrecipProbability() {
        return precipProbability;
    }

    public String getPrecipType() {
        return precipType;
    }

    public double getPressure() {
        return pressure;
    }

    public String getSummary() {
        return summary;
    }

    public long getSunriseTime() {
        return sunriseTime;
    }

    public long getSunsetTime() {
        return sunsetTime;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getTemperatureMax() {
        return temperatureMax;
    }

    public long getTemperatureMaxTime() {
        return temperatureMaxTime;
    }

    public double getTemperatureMin() {
        return temperatureMin;
    }

    public long getTemperatureMinTime() {
        return temperatureMinTime;
    }

    public long getTime() {
        return time;
    }

    public int getUvIndex() {
        return uvIndex;
    }

    public long getUvIndexTime() {
        return uvIndexTime;
    }

    public double getVisibility() {
        return visibility;
    }

    public int getWindBearing() {
        return windBearing;
    }

    public double getWindGust() {
        return windGust;
    }

    public long getWindGustTime() {
        return windGustTime;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getType() {
        return type;
    }
}
