package com.gautamastudios.whatweather;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.gautamastudios.whatweather.storage.WeatherDatabase;
import com.gautamastudios.whatweather.storage.dao.AlertDao;
import com.gautamastudios.whatweather.storage.dao.DataBlockDao;
import com.gautamastudios.whatweather.storage.dao.DataPointDao;
import com.gautamastudios.whatweather.storage.dao.FlagsDao;
import com.gautamastudios.whatweather.storage.dao.WeatherForecastDao;
import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.storage.model.DataPointType;
import com.gautamastudios.whatweather.storage.model.Flags;
import com.gautamastudios.whatweather.storage.model.WeatherForecast;
import com.gautamastudios.whatweather.util.GeneralUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DbEntityReadWriteTest {

    private String[] subjectsUnderTest = {"mockJsonResponse1.json", "mockJsonResponse2.json", "mockJsonResponse3.json"};

    private Context context;
    private WeatherDatabase weatherDatabase;

    private WeatherForecastDao weatherForecastDao;
    private DataPointDao dataPointDao;
    private DataBlockDao dataBlockDao;
    private AlertDao alertDao;
    private FlagsDao flagsDao;

    @Before
    public void createDb() {
        context = InstrumentationRegistry.getTargetContext();
        weatherDatabase = Room.inMemoryDatabaseBuilder(context, WeatherDatabase.class).build();
        weatherForecastDao = weatherDatabase.weatherForecastDao();
        dataPointDao = weatherDatabase.dataPointDao();
        dataBlockDao = weatherDatabase.dataBlockDao();
        alertDao = weatherDatabase.alertDao();
        flagsDao = weatherDatabase.flagsDao();
    }

    @After
    public void closeDb() throws IOException {
        weatherDatabase.close();
    }

    @Test
    public void assertValidJson() throws Exception {
        for (String subject : subjectsUnderTest) {
            String jsonData = GeneralUtils.readFileFromAssets(subject, context);
            boolean sut;
            try {
                new JSONObject(jsonData);
                sut = true;
            } catch (JSONException ex) {
                try {
                    new JSONArray(jsonData);
                    sut = true;
                } catch (JSONException ex1) {
                    sut = false;
                }
            }

            assertTrue(sut);
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
    public void writeReadWeatherForecast() throws Exception {
        for (String subject : subjectsUnderTest) {
            String jsonData = GeneralUtils.readFileFromAssets(subject, context);
            WeatherForecast weatherForecast = WeatherForecast.buildWeatherForecastFromResponse(jsonData);

            //WRITE
            weatherForecastDao.insert(weatherForecast);

            dataPointDao.insert(weatherForecast.getCurrently());

            dataPointDao.insert(weatherForecast.getMinutely().getData(DataPointType.MINUTELY));
            dataPointDao.insert(weatherForecast.getHourly().getData(DataPointType.HOURLY));
            dataPointDao.insert(weatherForecast.getDaily().getData(DataPointType.DAILY));

            dataBlockDao.insert(weatherForecast.getMinutely());
            dataBlockDao.insert(weatherForecast.getHourly());
            dataBlockDao.insert(weatherForecast.getDaily());

            alertDao.insert(weatherForecast.getAlerts());
            flagsDao.insert(weatherForecast.getFlags());

            //READ
            WeatherForecast wfTestResult = weatherForecastDao.queryTimeStampPrimaryKey(
                    weatherForecast.getTimeStampPrimaryKey());

            List<DataPoint> onlyOneDataPoint = dataPointDao.queryDataPointsWhere(DataPointType.CURRENTLY);
            assertFalse(onlyOneDataPoint.size() > 1);
            wfTestResult.setCurrently(onlyOneDataPoint.get(0));

            wfTestResult.setMinutely(dataBlockDao.queryDataBlockWhere(DataPointType.MINUTELY));
            wfTestResult.getMinutely().setData(dataPointDao.queryDataPointsWhere(DataPointType.MINUTELY));
            wfTestResult.setHourly(dataBlockDao.queryDataBlockWhere(DataPointType.HOURLY));
            wfTestResult.getHourly().setData(dataPointDao.queryDataPointsWhere(DataPointType.HOURLY));
            wfTestResult.setDaily(dataBlockDao.queryDataBlockWhere(DataPointType.DAILY));
            wfTestResult.getDaily().setData(dataPointDao.queryDataPointsWhere(DataPointType.DAILY));

            wfTestResult.setAlerts(alertDao.queryAll());

            List<Flags> onlyOneFlag = flagsDao.queryForFlags();
            assertFalse(onlyOneFlag.size() > 1);
            wfTestResult.setFlags(onlyOneFlag.get(0));

            //TEST
            assertEquals(wfTestResult.getTimeStampPrimaryKey(), weatherForecast.getTimeStampPrimaryKey());

            assertEquals(wfTestResult.getCurrently().getTime(), weatherForecast.getCurrently().getTime());
            assertEquals(wfTestResult.getCurrently().getType(), DataPointType.CURRENTLY);

            assertEquals(wfTestResult.getMinutely().getType(), DataPointType.MINUTELY);
            for (DataPoint dataPoint : wfTestResult.getMinutely().getData()) {
                assertEquals(dataPoint.getType(), DataPointType.MINUTELY);
            }

            assertEquals(wfTestResult.getHourly().getType(), DataPointType.HOURLY);
            for (DataPoint dataPoint : wfTestResult.getHourly().getData()) {
                assertEquals(dataPoint.getType(), DataPointType.HOURLY);
            }

            assertEquals(wfTestResult.getDaily().getType(), DataPointType.DAILY);
            for (DataPoint dataPoint : wfTestResult.getDaily().getData()) {
                assertEquals(dataPoint.getType(), DataPointType.DAILY);
            }

            //FINISH WRITE READ NOW CLEAR AND SYNC
            weatherForecastDao.deleteTable();
            dataPointDao.deleteTable();
            dataBlockDao.deleteTable();
            alertDao.deleteTable();
            flagsDao.deleteTable();
        }
    }

}
