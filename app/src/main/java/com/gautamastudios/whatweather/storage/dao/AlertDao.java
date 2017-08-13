package com.gautamastudios.whatweather.storage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import com.gautamastudios.whatweather.storage.model.Alert;

import java.util.List;

@Dao
public interface AlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(List<Alert> alert);

    @Query("SELECT * FROM " + Alert.TABLE_NAME)
    Cursor queryAll();

    @Query("DELETE FROM " + Alert.TABLE_NAME)
    int deleteTable();
}
