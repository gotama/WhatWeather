package com.gautamastudios.whatweather.api;

import org.json.JSONObject;

public interface APICallback {

    void onSuccess(JSONObject jsonObject);

    void onFail(String message, int code);
}
