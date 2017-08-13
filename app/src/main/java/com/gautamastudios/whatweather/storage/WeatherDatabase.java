package com.gautamastudios.whatweather.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.gautamastudios.whatweather.storage.dao.AlertDao;
import com.gautamastudios.whatweather.storage.dao.DataBlockDao;
import com.gautamastudios.whatweather.storage.dao.DataPointDao;
import com.gautamastudios.whatweather.storage.dao.FlagsDao;
import com.gautamastudios.whatweather.storage.dao.WeatherForecastDao;
import com.gautamastudios.whatweather.storage.model.Alert;
import com.gautamastudios.whatweather.storage.model.DataBlock;
import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.storage.model.Flags;
import com.gautamastudios.whatweather.storage.model.WeatherForecast;

@Database(entities = {WeatherForecast.class, Flags.class, DataPoint.class, DataBlock.class, Alert.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class WeatherDatabase extends RoomDatabase {

    static final String DATABASE_NAME = "weather-db";

    private static WeatherDatabase sInstance;

    /**
     * Gets the singleton instance of WeatherDatabase.
     *
     * @param context The context.
     * @return The singleton instance of WeatherDatabase.
     */
    public static synchronized WeatherDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(), WeatherDatabase.class, DATABASE_NAME)
                    .build();
        }
        return sInstance;
    }

    /**
     * Switches the internal implementation with an empty in-memory database.
     *
     * @param context The context.
     */
    @VisibleForTesting
    public static void switchToInMemory(Context context) {
        sInstance = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), WeatherDatabase.class).build();
    }

    public abstract WeatherForecastDao weatherForecastDao();

    public abstract DataPointDao dataPointDao();

    public abstract DataBlockDao dataBlockDao();

    public abstract AlertDao alertDao();

    public abstract FlagsDao flagsDao();
}
