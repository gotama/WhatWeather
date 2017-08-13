package com.gautamastudios.whatweather.storage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.storage.model.DataPointType;

import java.util.List;

@Dao
public interface DataPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DataPoint dataPoint);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(List<DataPoint> dataPoint);

    @Query("SELECT * FROM " + DataPoint.TABLE_NAME + " WHERE " + DataPoint.FIELD_DATA_POINT_TYPE + " = :type")
    Cursor queryDataPointsWhere(@DataPointType int type);

    @Query("DELETE FROM " + DataPoint.TABLE_NAME)
    int deleteTable();
}
