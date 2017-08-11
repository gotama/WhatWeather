package com.gautamastudios.whatweather.storage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.gautamastudios.whatweather.storage.model.DataPoint;
import com.gautamastudios.whatweather.storage.model.DataPointType;

import java.util.List;

import static com.gautamastudios.whatweather.storage.model.DataPoint.FIELD_DATA_POINT_TYPE;

@Dao
public interface DataPointDao {

    @Insert
    void insert(DataPoint dataPoint);

    @Insert
    void insert(List<DataPoint> dataPoint);

    @Query("SELECT * FROM datapoint WHERE " + FIELD_DATA_POINT_TYPE + " = :type")
    List<DataPoint> queryDataPointsWhere(@DataPointType int type);

    @Query("DELETE FROM datapoint")
    void deleteTable();
}
