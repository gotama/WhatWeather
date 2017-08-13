package com.gautamastudios.whatweather.storage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import com.gautamastudios.whatweather.storage.model.DataBlock;

@Dao
public interface DataBlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DataBlock dataBlock);

    @Query("SELECT * FROM " + DataBlock.TABLE_NAME + " WHERE " + DataBlock.FIELD_DATA_BLOCK_TYPE + " = :type")
    Cursor queryDataBlockWhere(int type);

    @Query("DELETE FROM " + DataBlock.TABLE_NAME)
    int deleteTable();
}
