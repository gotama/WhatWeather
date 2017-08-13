package com.gautamastudios.whatweather;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.gautamastudios.whatweather.storage.WeatherDatabase;
import com.gautamastudios.whatweather.storage.model.Alert;
import com.gautamastudios.whatweather.storage.model.DataBlock;
import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.storage.model.DataPointType;
import com.gautamastudios.whatweather.storage.model.Flags;
import com.gautamastudios.whatweather.storage.model.WeatherForecast;
import com.gautamastudios.whatweather.storage.provider.WeatherForecastProvider;
import com.gautamastudios.whatweather.util.GeneralUtils;
import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DbEntityReadWriteTest {

    private String[] subjectsUnderTest = {"mockJsonResponse1.json", "mockJsonResponse2.json", "mockJsonResponse3.json"};

    private Context context;
    private ContentResolver contentResolver;

    @Before
    public void createDb() {
        context = InstrumentationRegistry.getTargetContext();
        WeatherDatabase.switchToInMemory(context);
        contentResolver = context.getContentResolver();
    }

    @After
    public void closeDb() throws IOException {
        WeatherDatabase.getInstance(context).close();
    }

    @Test
    public void assertValidJson() throws Exception {
        for (String subject : subjectsUnderTest) {
            String jsonData = GeneralUtils.readFileFromAssets(subject, context);
            assertTrue(WeatherApplication.getInstance().isValidJson(jsonData));
        }
    }

    @Test
    public void parseMockJsonResponsesToPOJO() throws Exception {
        for (String subject : subjectsUnderTest) {
            String jsonData = GeneralUtils.readFileFromAssets(subject, context);

            WeatherForecast weatherForecast = new Gson().fromJson(jsonData, WeatherForecast.class);

            assertTrue(weatherForecast != null);
            assertTrue(weatherForecast.getCurrently() != null);
            assertTrue(weatherForecast.getMinutely() != null);
            assertTrue(weatherForecast.getHourly() != null);
            assertTrue(weatherForecast.getDaily() != null);
            assertTrue(weatherForecast.getAlerts() != null);
            assertTrue(weatherForecast.getFlags() != null);
        }
    }

    @Test
    public void writeReadDeleteWeatherForecast() throws Exception {
        for (String subject : subjectsUnderTest) {
            String jsonData = GeneralUtils.readFileFromAssets(subject, context);
            WeatherForecast weatherForecast = WeatherForecast.buildWeatherForecastFromResponse(jsonData);

            //WRITE
            final Uri weatherForecastUri = contentResolver.insert(
                    WeatherForecastProvider.getUriProvider(WeatherForecast.TABLE_NAME),
                    WeatherForecastProvider.getWeatherForecastValues(weatherForecast));
            assertThat(weatherForecastUri, notNullValue());
            assertThat(weatherForecastUri.toString(),
                    containsString(String.valueOf(weatherForecast.timeStampPrimaryKey)));
            assertEquals(ContentUris.parseId(weatherForecastUri), weatherForecast.timeStampPrimaryKey);
            long timeStampPrimaryKey = ContentUris.parseId(weatherForecastUri);

            final Uri currentlyDPUri = contentResolver.insert(
                    WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                    WeatherForecastProvider.getDataPointValues(weatherForecast.getCurrently()));
            assertThat(currentlyDPUri, notNullValue());

            final ContentValues[] minutelyValues = WeatherForecastProvider.getBulkInsertDataPointValues(
                    weatherForecast.getMinutely().getData(DataPointType.MINUTELY));
            int rowCount = contentResolver.bulkInsert(WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                    minutelyValues);
            assertThat(rowCount, is(minutelyValues.length));

            final ContentValues[] hourlyValues = WeatherForecastProvider.getBulkInsertDataPointValues(
                    weatherForecast.getMinutely().getData(DataPointType.HOURLY));
            rowCount = contentResolver.bulkInsert(WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                    hourlyValues);
            assertThat(rowCount, is(hourlyValues.length));

            final ContentValues[] dailyValues = WeatherForecastProvider.getBulkInsertDataPointValues(
                    weatherForecast.getMinutely().getData(DataPointType.DAILY));
            rowCount = contentResolver.bulkInsert(WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                    dailyValues);
            assertThat(rowCount, is(dailyValues.length));

            final Uri currentlyDBUri = contentResolver.insert(
                    WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME),
                    WeatherForecastProvider.getDataBlockValues(weatherForecast.getMinutely()));
            assertThat(currentlyDBUri, notNullValue());
            final Uri hourlyDBUri = contentResolver.insert(WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME),
                    WeatherForecastProvider.getDataBlockValues(weatherForecast.getHourly()));
            assertThat(hourlyDBUri, notNullValue());
            final Uri dailyDBUri = contentResolver.insert(WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME),
                    WeatherForecastProvider.getDataBlockValues(weatherForecast.getDaily()));
            assertThat(dailyDBUri, notNullValue());

            final ContentValues[] alertValues = new ContentValues[weatherForecast.getAlerts().size()];
            int count = 0;
            for (Alert alert : weatherForecast.getAlerts()) {
                alertValues[count] = WeatherForecastProvider.getAlertValues(alert);
                count++;
            }
            rowCount = contentResolver.bulkInsert(WeatherForecastProvider.getUriProvider(Alert.TABLE_NAME),
                    alertValues);
            assertThat(rowCount, is(alertValues.length));

            final Uri flagsUri = contentResolver.insert(WeatherForecastProvider.getUriProvider(Flags.TABLE_NAME),
                    WeatherForecastProvider.getFlagsValues(weatherForecast.getFlags()));
            assertThat(flagsUri, notNullValue());

            //READ
            final Cursor weatherForecastCursor = contentResolver.query(
                    WeatherForecastProvider.getUriProvider(WeatherForecast.TABLE_NAME),
                    new String[]{WeatherForecast.FIELD_PRIMARY_KEY}, null, null, null);
            assertThat(weatherForecastCursor, notNullValue());
            assertThat(weatherForecastCursor.getCount(), is(1));
            assertThat(weatherForecastCursor.moveToFirst(), is(true));
            assertThat(weatherForecastCursor
                            .getLong(weatherForecastCursor.getColumnIndexOrThrow(WeatherForecast.FIELD_PRIMARY_KEY)),
                    is(timeStampPrimaryKey));
            weatherForecastCursor.close();

            final Cursor currentlyDPCursor = contentResolver.query(
                    WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                    new String[]{DataPoint.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.CURRENTLY), null, null);
            assertThat(currentlyDPCursor, notNullValue());
            assertThat(currentlyDPCursor.getCount(), is(1));
            assertThat(currentlyDPCursor.moveToFirst(), is(true));
            assertThat(
                    currentlyDPCursor.getInt(currentlyDPCursor.getColumnIndexOrThrow(DataPoint.FIELD_DATA_POINT_TYPE)),
                    is(DataPointType.CURRENTLY));

            final Cursor minutelyDPCursor = contentResolver.query(
                    WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                    new String[]{DataPoint.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.MINUTELY), null, null);
            assertThat(minutelyDPCursor, notNullValue());
            assertThat(minutelyDPCursor.getCount(), is(minutelyValues.length));
            if (minutelyValues.length > 0) {
                assertThat(minutelyDPCursor.moveToFirst(), is(true));
            }
            while (minutelyDPCursor.moveToNext()) {
                assertThat(minutelyDPCursor
                                .getInt(currentlyDPCursor.getColumnIndexOrThrow(DataPoint.FIELD_DATA_POINT_TYPE)),
                        is(DataPointType.MINUTELY));
            }

            final Cursor hourlyDPCursor = contentResolver.query(
                    WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                    new String[]{DataPoint.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.HOURLY), null, null);
            assertThat(hourlyDPCursor, notNullValue());
            assertThat(hourlyDPCursor.getCount(), is(hourlyValues.length));
            if (hourlyValues.length > 0) {
                assertThat(hourlyDPCursor.moveToFirst(), is(true));
            }
            while (hourlyDPCursor.moveToNext()) {
                assertThat(
                        hourlyDPCursor.getInt(currentlyDPCursor.getColumnIndexOrThrow(DataPoint.FIELD_DATA_POINT_TYPE)),
                        is(DataPointType.HOURLY));
            }

            final Cursor dailyDPCursor = contentResolver.query(
                    WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                    new String[]{DataPoint.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.DAILY), null, null);
            assertThat(dailyDPCursor, notNullValue());
            assertThat(dailyDPCursor.getCount(), is(dailyValues.length));
            if (dailyValues.length > 0) {
                assertThat(dailyDPCursor.moveToFirst(), is(true));
            }
            while (dailyDPCursor.moveToNext()) {
                assertThat(
                        dailyDPCursor.getInt(currentlyDPCursor.getColumnIndexOrThrow(DataPoint.FIELD_DATA_POINT_TYPE)),
                        is(DataPointType.DAILY));
            }

            final Cursor minutelyDBCursor = contentResolver.query(
                    WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME),
                    new String[]{DataBlock.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.MINUTELY), null, null);
            assertThat(minutelyDBCursor, notNullValue());
            assertThat(minutelyDBCursor.getCount(), is(1));
            assertThat(minutelyDBCursor.moveToFirst(), is(true));
            assertThat(minutelyDBCursor.getInt(minutelyDBCursor.getColumnIndexOrThrow(DataBlock.FIELD_DATA_BLOCK_TYPE)),
                    is(DataPointType.MINUTELY));

            final Cursor hourlyDBCursor = contentResolver.query(
                    WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME),
                    new String[]{DataBlock.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.HOURLY), null, null);
            assertThat(hourlyDBCursor, notNullValue());
            assertThat(hourlyDBCursor.getCount(), is(1));
            assertThat(hourlyDBCursor.moveToFirst(), is(true));
            assertThat(hourlyDBCursor.getInt(hourlyDBCursor.getColumnIndexOrThrow(DataBlock.FIELD_DATA_BLOCK_TYPE)),
                    is(DataPointType.HOURLY));

            final Cursor dailyDBCursor = contentResolver.query(
                    WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME),
                    new String[]{DataBlock.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.DAILY), null, null);
            assertThat(dailyDBCursor, notNullValue());
            assertThat(dailyDBCursor.getCount(), is(1));
            assertThat(dailyDBCursor.moveToFirst(), is(true));
            assertThat(dailyDBCursor.getInt(dailyDBCursor.getColumnIndexOrThrow(DataBlock.FIELD_DATA_BLOCK_TYPE)),
                    is(DataPointType.DAILY));

            final Cursor alertsCursor = contentResolver.query(WeatherForecastProvider.getUriProvider(Alert.TABLE_NAME),
                    new String[]{Alert.FIELD_PRIMARY_KEY}, null, null, null);
            assertThat(alertsCursor, notNullValue());
            assertThat(alertsCursor.getCount(), is(alertValues.length));
            if (alertValues.length > 0) {
                assertThat(alertsCursor.moveToFirst(), is(true));
            }

            final Cursor flagsCursor = contentResolver.query(WeatherForecastProvider.getUriProvider(Flags.TABLE_NAME),
                    new String[]{Flags.FIELD_PRIMARY_KEY}, null, null, null);
            assertThat(flagsCursor, notNullValue());
            assertThat(flagsCursor.getCount(), is(1));
            assertThat(flagsCursor.moveToFirst(), is(true));

            //DELETE
            final int wfCount = contentResolver.delete(
                    WeatherForecastProvider.getUriProvider(WeatherForecast.TABLE_NAME), null, null);
            assertThat(wfCount, is(1));

            final int dpCount = contentResolver.delete(WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                    null, null);
            assertThat(dpCount, is(1 + minutelyValues.length + hourlyValues.length + dailyValues.length));

            final int dbCount = contentResolver.delete(WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME),
                    null, null);
            assertThat(dbCount, is(3));

            final int alertCount = contentResolver.delete(WeatherForecastProvider.getUriProvider(Alert.TABLE_NAME),
                    null, null);
            if (weatherForecast.getAlerts().size() > 0) {
                assertThat(alertCount, is(alertValues.length));
            }

            final int flagCount = contentResolver.delete(WeatherForecastProvider.getUriProvider(Flags.TABLE_NAME), null,
                    null);
            assertThat(flagCount, is(1));

            final Cursor wfQueryTest = contentResolver.query(
                    WeatherForecastProvider.getUriProvider(WeatherForecast.TABLE_NAME),
                    new String[]{WeatherForecast.FIELD_PRIMARY_KEY}, null, null, null);
            assertThat(wfQueryTest, notNullValue());
            assertThat(wfQueryTest.getCount(), is(0));
            wfQueryTest.close();

            final Cursor dpQueryTest = contentResolver.query(
                    WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                    new String[]{DataPoint.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.CURRENTLY), null, null);
            assertThat(dpQueryTest, notNullValue());
            assertThat(dpQueryTest.getCount(), is(0));
            dpQueryTest.close();

            final Cursor mQueryTest = contentResolver.query(
                    WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                    new String[]{DataPoint.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.MINUTELY), null, null);
            assertThat(mQueryTest, notNullValue());
            assertThat(mQueryTest.getCount(), is(0));
            mQueryTest.close();

            final Cursor hQueryTest = contentResolver.query(
                    WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                    new String[]{DataPoint.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.HOURLY), null, null);
            assertThat(hQueryTest, notNullValue());
            assertThat(hQueryTest.getCount(), is(0));
            hQueryTest.close();

            final Cursor dQueryTest = contentResolver.query(
                    WeatherForecastProvider.getUriProvider(DataPoint.TABLE_NAME),
                    new String[]{DataPoint.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.DAILY), null, null);
            assertThat(dQueryTest, notNullValue());
            assertThat(dQueryTest.getCount(), is(0));
            dQueryTest.close();

            Cursor dbQueryTest = contentResolver.query(WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME),
                    new String[]{DataBlock.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.MINUTELY), null, null);
            assertThat(dbQueryTest, notNullValue());
            assertThat(dbQueryTest.getCount(), is(0));
            dbQueryTest.close();

            dbQueryTest = contentResolver.query(WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME),
                    new String[]{DataBlock.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.HOURLY), null, null);
            assertThat(dbQueryTest, notNullValue());
            assertThat(dbQueryTest.getCount(), is(0));
            dbQueryTest.close();

            dbQueryTest = contentResolver.query(WeatherForecastProvider.getUriProvider(DataBlock.TABLE_NAME),
                    new String[]{DataBlock.FIELD_PRIMARY_KEY}, String.valueOf(DataPointType.DAILY), null, null);
            assertThat(dbQueryTest, notNullValue());
            assertThat(dbQueryTest.getCount(), is(0));
            dbQueryTest.close();

            Cursor alertsQueryTest = contentResolver.query(WeatherForecastProvider.getUriProvider(Alert.TABLE_NAME),
                    new String[]{Alert.FIELD_PRIMARY_KEY}, null, null, null);
            assertThat(alertsQueryTest, notNullValue());
            assertThat(alertsQueryTest.getCount(), is(0));
            alertsQueryTest.close();

            Cursor flagsQueryTest = contentResolver.query(WeatherForecastProvider.getUriProvider(Flags.TABLE_NAME),
                    new String[]{Flags.FIELD_PRIMARY_KEY}, null, null, null);
            assertThat(flagsQueryTest, notNullValue());
            assertThat(flagsQueryTest.getCount(), is(0));
            flagsQueryTest.close();
        }
    }

}
