package com.gautamastudios.whatweather.storage.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({DataPointType.CURRENTLY, DataPointType.MINUTELY, DataPointType.HOURLY, DataPointType.DAILY})
@Retention(RetentionPolicy.SOURCE)
public @interface DataPointType {

    int CURRENTLY = 1;
    int MINUTELY = 2;
    int HOURLY = 3;
    int DAILY = 4;
}
