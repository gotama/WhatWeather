package com.gautamastudios.whatweather.api;


public class DarkSkyAPI {

    private static DarkSkyAPI darkSkyAPI;
    private static final String BASE_URL = "http://ec-weather-proxy.appspot.com/forecast/";

    public static DarkSkyAPI getInstance() {
        if (darkSkyAPI == null) {
            //TODO Handle negative flow
        }

        return darkSkyAPI;
    }

    public static void initializeAPI() {
        darkSkyAPI = new DarkSkyAPI();
    }
}
