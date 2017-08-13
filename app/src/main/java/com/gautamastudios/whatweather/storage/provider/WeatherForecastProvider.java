package com.gautamastudios.whatweather.storage.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gautamastudios.whatweather.storage.WeatherDatabase;
import com.gautamastudios.whatweather.storage.model.Alert;
import com.gautamastudios.whatweather.storage.model.DataBlock;
import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.storage.model.DataPointType;
import com.gautamastudios.whatweather.storage.model.Flags;
import com.gautamastudios.whatweather.storage.model.WeatherForecast;

import java.util.ArrayList;
import java.util.List;

public class WeatherForecastProvider extends ContentProvider {

    private static final String TAG = WeatherForecastProvider.class.getSimpleName();

    private static final String AUTHORITY = "com.gautamastudios.whatweather.storage.provider";
    private static final String URI_DOMAIN = "content://";
    private static final String URI_FORWARD_SLASH = "/";

    /**
     * @param tableName constant value from table object
     * @return Uri for provider
     */
    public static Uri getUriProvider(String tableName) {
        return Uri.parse(URI_DOMAIN + AUTHORITY + URI_FORWARD_SLASH + tableName);
    }

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int CODE_WEATHER_TABLE = 100;
    private static final int CODE_DATA_POINT_TABLE = 200;
    private static final int CODE_DATA_BLOCK_TABLE = 300;
    private static final int CODE_ALERT_TABLE = 400;
    private static final int CODE_FLAGS_TABLE = 500;

    static {
        MATCHER.addURI(AUTHORITY, WeatherForecast.TABLE_NAME, CODE_WEATHER_TABLE);
        MATCHER.addURI(AUTHORITY, DataPoint.TABLE_NAME, CODE_DATA_POINT_TABLE);
        MATCHER.addURI(AUTHORITY, DataBlock.TABLE_NAME, CODE_DATA_BLOCK_TABLE);
        MATCHER.addURI(AUTHORITY, Alert.TABLE_NAME, CODE_ALERT_TABLE);
        MATCHER.addURI(AUTHORITY, Flags.TABLE_NAME, CODE_FLAGS_TABLE);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    /**
     * @param uri           Uri maps to the table in the provider named table_name.
     * @param projection    projection is an array of columns that should be included for each row retrieved.
     * @param selection     selection specifies the criteria for selecting rows.
     * @param selectionArgs No exact equivalent. Selection arguments replace ? placeholders in the selection clause.
     * @param sortOrder     sortOrder specifies the order in which rows appear in the returned Cursor.
     * @return a Cursor or null.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
            @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final Context context = getContext();
        if (context == null) {
            return null;
        }
        final Cursor cursor;

        switch (MATCHER.match(uri)) {
            case CODE_WEATHER_TABLE:
                cursor = WeatherDatabase.getInstance(context).weatherForecastDao().query();
                cursor.setNotificationUri(context.getContentResolver(), uri);
                return cursor;
            case CODE_DATA_POINT_TABLE:
                @DataPointType int dpType = Integer.valueOf(selection);
                cursor = WeatherDatabase.getInstance(context).dataPointDao().queryDataPointsWhere(dpType);
                cursor.setNotificationUri(context.getContentResolver(), uri);
                return cursor;
            case CODE_DATA_BLOCK_TABLE:
                @DataPointType int dbType = Integer.valueOf(selection);
                cursor = WeatherDatabase.getInstance(context).dataBlockDao().queryDataBlockWhere(dbType);
                cursor.setNotificationUri(context.getContentResolver(), uri);
                return cursor;
            case CODE_ALERT_TABLE:
                cursor = WeatherDatabase.getInstance(context).alertDao().queryAll();
                cursor.setNotificationUri(context.getContentResolver(), uri);
                return cursor;
            case CODE_FLAGS_TABLE:
                cursor = WeatherDatabase.getInstance(context).flagsDao().queryForFlags();
                cursor.setNotificationUri(context.getContentResolver(), uri);
                return cursor;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final Context context = getContext();
        if (context == null) {
            return null;
        }
        final long id;
        switch (MATCHER.match(uri)) {
            case CODE_WEATHER_TABLE:
                id = WeatherDatabase.getInstance(context).weatherForecastDao().insert(
                        WeatherForecast.fromContentValues(contentValues));
                context.getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            case CODE_DATA_POINT_TABLE:
                id = WeatherDatabase.getInstance(context).dataPointDao().insert(
                        DataPoint.fromContentValues(contentValues));
                context.getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            case CODE_DATA_BLOCK_TABLE:
                id = WeatherDatabase.getInstance(context).dataBlockDao().insert(
                        DataBlock.fromContentValues(contentValues));
                context.getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            case CODE_FLAGS_TABLE:
                id = WeatherDatabase.getInstance(context).flagsDao().insert(Flags.fromContentValues(contentValues));
                context.getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }

    /**
     * @param uri
     * @param valuesArray
     * @return count of rows inserted
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] valuesArray) {
        final Context context = getContext();
        if (context == null) {
            return 0;
        }

        switch (MATCHER.match(uri)) {
            case CODE_DATA_POINT_TABLE:
                final List<DataPoint> dataPoints = new ArrayList<>();
                for (ContentValues values : valuesArray) {
                    dataPoints.add(DataPoint.fromContentValues(values));
                }

                return WeatherDatabase.getInstance(context).dataPointDao().insert(dataPoints).length;
            case CODE_ALERT_TABLE:
                final List<Alert> alerts = new ArrayList<>();
                for (ContentValues values : valuesArray) {
                    alerts.add(Alert.fromContentValues(values));
                }

                return WeatherDatabase.getInstance(context).alertDao().insert(alerts).length;
            case CODE_FLAGS_TABLE:
            case CODE_WEATHER_TABLE:
            case CODE_DATA_BLOCK_TABLE:
                throw new IllegalArgumentException("Invalid URI, cannot bulk insert with URI: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final Context context = getContext();
        if (context == null) {
            return -1;
        }
        final int count;
        switch (MATCHER.match(uri)) {
            case CODE_WEATHER_TABLE:
                count = WeatherDatabase.getInstance(context).weatherForecastDao().deleteTable();
                context.getContentResolver().notifyChange(uri, null);
                return count;
            case CODE_DATA_POINT_TABLE:
                count = WeatherDatabase.getInstance(context).dataPointDao().deleteTable();
                context.getContentResolver().notifyChange(uri, null);
                return count;
            case CODE_DATA_BLOCK_TABLE:
                count = WeatherDatabase.getInstance(context).dataBlockDao().deleteTable();
                context.getContentResolver().notifyChange(uri, null);
                return count;
            case CODE_ALERT_TABLE:
                count = WeatherDatabase.getInstance(context).alertDao().deleteTable();
                context.getContentResolver().notifyChange(uri, null);
                return count;
            case CODE_FLAGS_TABLE:
                count = WeatherDatabase.getInstance(context).flagsDao().deleteTable();
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
            @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case CODE_WEATHER_TABLE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + WeatherForecast.TABLE_NAME;
            case CODE_DATA_POINT_TABLE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + DataPoint.TABLE_NAME;
            case CODE_DATA_BLOCK_TABLE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + DataBlock.TABLE_NAME;
            case CODE_ALERT_TABLE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + Alert.TABLE_NAME;
            case CODE_FLAGS_TABLE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + Flags.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown Type : " + uri);
        }
    }

    public static ContentValues getWeatherForecastValues(WeatherForecast weatherForecast) {
        final ContentValues values = new ContentValues();
        values.put(WeatherForecast.FIELD_PRIMARY_KEY, weatherForecast.timeStampPrimaryKey);
        values.put(WeatherForecast.FIELD_LATITUDE, weatherForecast.latitude);
        values.put(WeatherForecast.FIELD_LONGITUDE, weatherForecast.longitude);
        values.put(WeatherForecast.FIELD_TIMEZONE, weatherForecast.timezone);
        values.put(WeatherForecast.FIELD_OFFSET, weatherForecast.offset);
        return values;
    }

    public static ContentValues getDataBlockValues(DataBlock dataBlock) {
        final ContentValues values = new ContentValues();
        values.put(DataBlock.FIELD_PRIMARY_KEY, dataBlock.autoGeneratedID);
        values.put(DataBlock.FIELD_SUMMARY, dataBlock.summary);
        values.put(DataBlock.FIELD_ICON, dataBlock.icon);
        values.put(DataBlock.FIELD_DATA_BLOCK_TYPE, dataBlock.type);
        return values;
    }

    public static ContentValues getDataPointValues(DataPoint dataPoint) {
        final ContentValues values = new ContentValues();
        values.put(DataPoint.FIELD_PRIMARY_KEY, dataPoint.autoGeneratedID);
        values.put(DataPoint.FIELD_APPARENT_TEMPERATURE, dataPoint.apparentTemperature);
        values.put(DataPoint.FIELD_APPARENT_TEMPERATURE_MAX, dataPoint.apparentTemperatureMax);
        values.put(DataPoint.FIELD_APPARENT_TEMPERATURE_MAX_TIME, dataPoint.apparentTemperatureMaxTime);
        values.put(DataPoint.FIELD_APPARENT_TEMPERATURE_MIN, dataPoint.apparentTemperatureMin);
        values.put(DataPoint.FIELD_APPARENT_TEMPERATURE_MIN_TIME, dataPoint.apparentTemperatureMinTime);
        values.put(DataPoint.FIELD_CLOUD_COVER, dataPoint.cloudCover);
        values.put(DataPoint.FIELD_DEW_POINT, dataPoint.dewPoint);
        values.put(DataPoint.FIELD_HUMIDITY, dataPoint.humidity);
        values.put(DataPoint.FIELD_ICON, dataPoint.icon);
        values.put(DataPoint.FIELD_MOON_PHASE, dataPoint.moonPhase);
        values.put(DataPoint.FIELD_NEAREST_STORM_BEARING, dataPoint.nearestStormBearing);
        values.put(DataPoint.FIELD_NEAREST_STORM_DISTANCE, dataPoint.nearestStormDistance);
        values.put(DataPoint.FIELD_OZONE, dataPoint.ozone);
        values.put(DataPoint.FIELD_PRECIP_ACCUMULATION, dataPoint.precipAccumulation);
        values.put(DataPoint.FIELD_PRECIP_INTENSITY, dataPoint.precipIntensity);
        values.put(DataPoint.FIELD_PRECIP_INTENSITY_MAX, dataPoint.precipIntensityMax);
        values.put(DataPoint.FIELD_PRECIP_INTENSITY_MAX_TIME, dataPoint.precipIntensityMaxTime);
        values.put(DataPoint.FIELD_PRECIP_PROBABILITY, dataPoint.precipProbability);
        values.put(DataPoint.FIELD_PRECIP_TYPE, dataPoint.precipType);
        values.put(DataPoint.FIELD_PRESSURE, dataPoint.pressure);
        values.put(DataPoint.FIELD_SUMMARY, dataPoint.summary);
        values.put(DataPoint.FIELD_SUNRISE_TIME, dataPoint.sunriseTime);
        values.put(DataPoint.FIELD_SUNSET_TIME, dataPoint.sunsetTime);
        values.put(DataPoint.FIELD_TEMPERATURE, dataPoint.temperature);
        values.put(DataPoint.FIELD_TEMPERATURE_MAX, dataPoint.temperatureMax);
        values.put(DataPoint.FIELD_TEMPERATURE_MAX_TIME, dataPoint.temperatureMaxTime);
        values.put(DataPoint.FIELD_TEMPERATURE_MIN, dataPoint.temperatureMin);
        values.put(DataPoint.FIELD_TEMPERATURE_MIN_TIME, dataPoint.temperatureMinTime);
        values.put(DataPoint.FIELD_TIME, dataPoint.time);
        values.put(DataPoint.FIELD_UV_INDEX, dataPoint.uvIndex);
        values.put(DataPoint.FIELD_UV_INDEX_TIME, dataPoint.uvIndexTime);
        values.put(DataPoint.FIELD_VISIBILITY, dataPoint.visibility);
        values.put(DataPoint.FIELD_WIND_BEARING, dataPoint.windBearing);
        values.put(DataPoint.FIELD_WIND_GUST, dataPoint.windGust);
        values.put(DataPoint.FIELD_WIND_GUST_TIME, dataPoint.windGustTime);
        values.put(DataPoint.FIELD_WIND_SPEED, dataPoint.windSpeed);
        values.put(DataPoint.FIELD_DATA_POINT_TYPE, dataPoint.type);

        return values;
    }

    public static ContentValues getAlertValues(Alert alert) {
        final ContentValues values = new ContentValues();
        values.put(Alert.FIELD_PRIMARY_KEY, alert.autoGeneratedKey);
        values.put(Alert.FIELD_DESCRIPTION, alert.description);
        values.put(Alert.FIELD_EXPIRES, alert.expires);
        values.put(Alert.FIELD_REGIONS, alert.regions);
        values.put(Alert.FIELD_SEVERITY, alert.severity);
        values.put(Alert.FIELD_TIME, alert.time);
        values.put(Alert.FIELD_TITLE, alert.title);
        values.put(Alert.FIELD_URI, alert.uri);
        return values;
    }

    public static ContentValues getFlagsValues(Flags flags) {
        final ContentValues values = new ContentValues();
        values.put(Flags.FIELD_PRIMARY_KEY, flags.autoGeneratedKey);
        values.put(Flags.FIELD_API_UNAVAILABLE, flags.darkskyUnavailable);
        values.put(Flags.FIELD_SOURCES, Flags.convertArrayToString(flags.getSources()));
        values.put(Flags.FIELD_UNITS, flags.units);
        return values;
    }

    public static ContentValues[] getBulkInsertDataPointValues(List<DataPoint> dataPoints) {
        final ContentValues[] contentValues = new ContentValues[dataPoints.size()];
        int count = 0;
        for (DataPoint dataPoint : dataPoints) {
            contentValues[count] = WeatherForecastProvider.getDataPointValues(dataPoint);
            count++;
        }

        return contentValues;
    }

    public static ContentValues[] getBulkInsertAlertValues(List<Alert> alerts) {
        final ContentValues[] contentValues = new ContentValues[alerts.size()];
        int count = 0;
        for (Alert alert : alerts) {
            contentValues[count] = WeatherForecastProvider.getAlertValues(alert);
            count++;
        }

        return contentValues;
    }
}
