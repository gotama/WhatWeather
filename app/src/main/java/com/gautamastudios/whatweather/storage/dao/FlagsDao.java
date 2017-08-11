package com.gautamastudios.whatweather.storage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.gautamastudios.whatweather.storage.model.Flags;

import java.util.List;

@Dao
public interface FlagsDao {

    @Insert
    void insert(Flags flags);

    @Query("SELECT * FROM flags")
    List<Flags> queryForFlags();

    @Query("DELETE FROM flags")
    void deleteTable();
}
