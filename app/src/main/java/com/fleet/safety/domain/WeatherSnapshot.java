package com.fleet.safety.domain;
public class WeatherSnapshot {
    private final double temperatureCelsius;
    private final double precipitationMm;
    private final WeatherType weatherType;

    public WeatherSnapshot(double temperatureCelsius, double precipitationMm, WeatherType weatherType) {
        this.temperatureCelsius = temperatureCelsius;
        this.precipitationMm = precipitationMm;
        this.weatherType = weatherType;
    }

    public double getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public double getPrecipitationMm() {
        return precipitationMm;
    }

    public WeatherType getWeatherType() {
        return weatherType;
    }
}
