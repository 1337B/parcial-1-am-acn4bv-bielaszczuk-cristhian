package com.fleet.safety.data.remote;

import com.fleet.safety.domain.WeatherSnapshot;
import com.fleet.safety.domain.WeatherType;

public class WeatherMapper {

    /**
     * Maps weather API data to a WeatherType based on temperature, precipitation, and weather code.
     *
     * @param temperatureCelsius Temperature in Celsius
     * @param precipitationMm Precipitation amount in millimeters
     * @param weatherCode WMO weather code from Open-Meteo API
     * @return WeatherSnapshot with mapped weather type
     */
    public static WeatherSnapshot mapFrom(double temperatureCelsius, double precipitationMm, int weatherCode) {
        WeatherType weatherType;

        if (temperatureCelsius <= 0) {
            weatherType = WeatherType.ICE;
        }
        else if (isSnowOrIceWeatherCode(weatherCode)) {
            weatherType = temperatureCelsius <= 0 ? WeatherType.ICE : WeatherType.SNOW;
        }

        else if (precipitationMm > 0) {
            weatherType = WeatherType.RAIN;
        }

        else {
            weatherType = WeatherType.CLEAR;
        }

        return new WeatherSnapshot(temperatureCelsius, precipitationMm, weatherType);
    }

    /**
     * Checks if the given weather code indicates snow or ice conditions.
     * Based on WMO weather codes used by Open-Meteo API.
     *
     * @param weatherCode The WMO weather code
     * @return true if the code indicates snow or ice conditions
     */
    private static boolean isSnowOrIceWeatherCode(int weatherCode) {
        return (weatherCode >= 71 && weatherCode <= 77) ||
               (weatherCode >= 85 && weatherCode <= 86);
    }
}
