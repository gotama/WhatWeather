package com.gautamastudios.whatweather.api;

import android.net.Uri;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gautamastudios.whatweather.WeatherApplication;
import com.gautamastudios.whatweather.logger.WeatherLog;

import org.json.JSONObject;

import java.util.Locale;

public class DarkSkyAPI {

    public static final String EXCLUDE_MINUTELY = "minutely";
    public static final String EXCLUDE_HOURLY = "hourly";
    public static final String EXCLUDE_DAILY = "daily";
    public static final String EXCLUDE_ALERTS = "alerts";
    public static final String EXCLUDE_FLAGS = "flags";

    private static final String TAG = DarkSkyAPI.class.getSimpleName();
    private static final String BASE_URL = "http://ec-weather-proxy.appspot.com/forecast/";
    private static final String ERROR_MESSAGE = "Critical Error, No response";

    //TODO handle robustly
    private static final double LATITUDE = -33.9249;
    private static final double LONGITUDE = 18.4241;

    //TODO unit tests
    private static final int DELAY = 5;
    private static final double CHAOS = 0;

    private static String buildURL(double latitude, double longitude, String[] exclude) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority("ec-weather-proxy.appspot.com").appendPath("forecast").appendPath(
                WeatherApplication.getKey()).appendPath(latitude + "," + longitude);

        if (exclude.length > 0) {
            for (int i = 0; i < exclude.length; i++) {
                builder.appendQueryParameter("exclude", exclude[i]);
            }
        }

        builder.appendQueryParameter("units", "si");
        builder.appendQueryParameter("delay", String.valueOf(DELAY));
        builder.appendQueryParameter("chaos", String.valueOf(CHAOS));

        return builder.build().toString();
    }

    public static void getJsonObjectForecast(final APICallback apiCallback, String[] exclude) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, buildURL(LATITUDE, LONGITUDE, exclude),
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    WeatherLog.d(TAG, "NetworkJournal", response.toString());
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
