package com.gautamastudios.whatweather.storage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import com.gautamastudios.whatweather.storage.model.WeatherForecast;

@Dao
public interface WeatherForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(WeatherForecast weatherForecast);

    @Query("SELECT * FROM " + WeatherForecast.TABLE_NAME)
    Cursor query();

    @Query("DELETE FROM " + WeatherForecast.TABLE_NAME)
    int deleteTable();
}
