package com.gautamastudios.whatweather.storage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.gautamastudios.whatweather.storage.model.WeatherForecast;

import static com.gautamastudios.whatweather.storage.model.WeatherForecast.FIELD_PRIMARY_KEY;

@Dao
public interface WeatherForecastDao {

    @Insert
    void insert(WeatherForecast weatherForecast);

    @Query("SELECT * FROM weather WHERE " + FIELD_PRIMARY_KEY + " = :unixTimeStamp")
    WeatherForecast queryTimeStampPrimaryKey(long unixTimeStamp);

    @Query("DELETE FROM weather")
    void deleteTable();
}
