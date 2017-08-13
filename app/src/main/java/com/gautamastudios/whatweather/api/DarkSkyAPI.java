package com.gautamastudios.whatweather.api;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gautamastudios.whatweather.WeatherApplication;

import org.json.JSONObject;

import java.util.Locale;

public class DarkSkyAPI {

    private static final String TAG = DarkSkyAPI.class.getSimpleName();
    private static final String BASE_URL = "http://ec-weather-proxy.appspot.com/forecast/";
    private static final String ERROR_MESSAGE = "Critical Error, No response";

    //TODO handle robustly
    private static final double LATITUDE = -33.9249;
    private static final double LONGITUDE = 18.4241;

    //TODO unit tests
    private static final int DELAY = 5;
    private static final double CHAOS = 0;

    private static String buildURL(double latitude, double longitude) {
        return BASE_URL + WeatherApplication.getKey() + "/" + latitude + "," + longitude + "?delay=" + DELAY + "&chaos=" + CHAOS;
    }

    public static void getJsonObjectForecast(final APICallback apiCallback) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, buildURL(LATITUDE, LONGITUDE), null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            //TODO add logging to callback
                            Log.d(TAG, response.toString());
                            apiCallback.onSuccess(response);
                        } else {
                            apiCallback.onFail(ERROR_MESSAGE, -1);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                String message;
                int responseCode;
                if (error.networkResponse != null) {
                    responseCode = error.networkResponse.statusCode;
                    message = String.format(Locale.getDefault(), "%s : %d", error.getMessage(), responseCode);
                } else {
                    //TODO check for -1 on UI
                    responseCode = -1;
                    message = ERROR_MESSAGE;
                }

                apiCallback.onFail(message, responseCode);
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        WeatherApplication.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
    }
}
