package com.gautamastudios.whatweather.storage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.gautamastudios.whatweather.storage.model.DataBlock;

import static com.gautamastudios.whatweather.storage.model.DataBlock.FIELD_DATA_BLOCK_TYPE;

@Dao
public interface DataBlockDao {

    @Insert
    void insert(DataBlock dataBlock);

    @Query("SELECT * FROM datablock WHERE " + FIELD_DATA_BLOCK_TYPE + " = :type")
    DataBlock queryDataBlockWhere(int type);

    @Query("DELETE FROM datablock")
    void deleteTable();
}
