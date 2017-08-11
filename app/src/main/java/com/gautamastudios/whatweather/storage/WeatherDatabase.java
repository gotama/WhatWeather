package com.gautamastudios.whatweather.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

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

    public abstract WeatherForecastDao weatherForecastDao();

    public abstract DataPointDao dataPointDao();

    public abstract DataBlockDao dataBlockDao();

    public abstract AlertDao alertDao();

    public abstract FlagsDao flagsDao();
}
