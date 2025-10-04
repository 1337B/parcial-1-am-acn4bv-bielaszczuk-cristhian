package com.fleet.safety.data.remote;
public interface WeatherService {

    /**
     * Retrieves current weather data for the specified coordinates asynchronously.
     *
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param callback The callback to handle success or error responses
     */
    void getCurrentAsync(double latitude, double longitude, WeatherCallback callback);
}
