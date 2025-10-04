package com.fleet.safety.data.remote;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OpenMeteoWeatherService implements WeatherService {

    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String CURRENT_PARAMS = "temperature_2m,precipitation,weather_code";

    private static final double COMODORO_LATITUDE = -45.86;
    private static final double COMODORO_LONGITUDE = -67.48;

    private final ExecutorService executorService;
    private final Handler mainHandler;

    public OpenMeteoWeatherService() {
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void getCurrentAsync(double latitude, double longitude, WeatherCallback callback) {
        executorService.execute(() -> {
            try {
                String urlString = buildUrl(latitude, longitude);
                String jsonResponse = performHttpRequest(urlString);
                com.fleet.safety.domain.WeatherSnapshot snapshot = parseWeatherResponse(jsonResponse);

                mainHandler.post(() -> callback.onSuccess(snapshot));

            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    public void getCurrentAsyncForComodoro(WeatherCallback callback) {
        getCurrentAsync(COMODORO_LATITUDE, COMODORO_LONGITUDE, callback);
    }

    private String buildUrl(double latitude, double longitude) {
        return BASE_URL +
               "?latitude=" + latitude +
               "&longitude=" + longitude +
               "&current=" + CURRENT_PARAMS;
    }

    private String performHttpRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP request failed with code: " + responseCode);
            }

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();

        } finally {
            connection.disconnect();
        }
    }

    private com.fleet.safety.domain.WeatherSnapshot parseWeatherResponse(String jsonResponse) throws JSONException {
        JSONObject root = new JSONObject(jsonResponse);
        JSONObject current = root.getJSONObject("current");

        double temperature = current.getDouble("temperature_2m");
        double precipitation = current.optDouble("precipitation", 0.0);
        int weatherCode = current.getInt("weather_code");

        return WeatherMapper.mapFrom(temperature, precipitation, weatherCode);
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
