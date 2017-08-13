package com.gautamastudios.whatweather.service;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({NetworkType.NETWORK_TYPE_NONE, NetworkType.NETWORK_TYPE_ANY, NetworkType.NETWORK_TYPE_UNMETERED,
                NetworkType.NETWORK_TYPE_NOT_ROAMING, NetworkType.NETWORK_TYPE_METERED})
@Retention(RetentionPolicy.SOURCE)
public @interface NetworkType {

    int NETWORK_TYPE_NONE = 0;
    int NETWORK_TYPE_ANY = 1;
    int NETWORK_TYPE_UNMETERED = 2;
    int NETWORK_TYPE_NOT_ROAMING = 3;
    int NETWORK_TYPE_METERED = 4;
}
