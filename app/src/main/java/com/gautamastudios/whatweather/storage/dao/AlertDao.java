package com.gautamastudios.whatweather.storage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.gautamastudios.whatweather.storage.model.Alert;

import java.util.List;

@Dao
public interface AlertDao {

    @Insert
    void insert(List<Alert> alert);

    @Query("SELECT * FROM alert")
    List<Alert> queryAll();

    @Query("DELETE FROM alert")
    void deleteTable();
}
