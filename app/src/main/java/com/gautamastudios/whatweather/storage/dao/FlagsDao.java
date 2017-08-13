package com.gautamastudios.whatweather.storage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import com.gautamastudios.whatweather.storage.model.Flags;

@Dao
public interface FlagsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Flags flags);

    @Query("SELECT * FROM " + Flags.TABLE_NAME)
    Cursor queryForFlags();

    @Query("DELETE FROM " + Flags.TABLE_NAME)
    int deleteTable();
}
