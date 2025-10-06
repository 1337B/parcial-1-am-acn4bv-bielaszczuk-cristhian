package com.fleet.safety.ui;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fleet.safety.data.remote.OpenMeteoWeatherService;
import com.fleet.safety.data.remote.WeatherCallback;
import com.fleet.safety.databinding.ActivityDriverDashboardBinding;
import com.fleet.safety.domain.DriverSettings;
import com.fleet.safety.domain.RoadType;
import com.fleet.safety.domain.SpeedDecision;
import com.fleet.safety.domain.SpeedRuleEngine;
import com.fleet.safety.domain.TimeOfDay;
import com.fleet.safety.domain.WeatherSnapshot;
import com.fleet.safety.domain.WeatherType;

import java.util.Locale;

public class DriverDashboardActivity extends AppCompatActivity {

    private static final String TAG = "DriverDashboard";

    private ActivityDriverDashboardBinding binding;
    private SettingsStore settingsStore;
    private SpeedRuleEngine ruleEngine;
    private OpenMeteoWeatherService weatherService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDriverDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsStore = new SettingsStore(this);
        ruleEngine = new SpeedRuleEngine();
        weatherService = new OpenMeteoWeatherService();

        setupSpinner();
        setupRecalculateButton();
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Asphalt", "Gravel"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRoad.setAdapter(adapter);
    }

    private void setupRecalculateButton() {
        binding.buttonRecalculate.setOnClickListener(v -> recalculate());
    }

    private void recalculate() {

        binding.buttonRecalculate.setEnabled(false);

        if (binding.switchOfflineWeather.isChecked()) {
            WeatherSnapshot offlineWeather = new WeatherSnapshot(15.0, 0.0, WeatherType.CLEAR);
            updateWeatherDisplay(offlineWeather);
            computeSpeed(offlineWeather);
        } else {
            weatherService.getCurrentAsyncForComodoro(new WeatherCallback() {
                @Override
                public void onSuccess(WeatherSnapshot snapshot) {
                    updateWeatherDisplay(snapshot);
                    computeSpeed(snapshot);
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Weather fetch failed", e);
                    Toast.makeText(DriverDashboardActivity.this,
                            "Weather error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    WeatherSnapshot fallback = new WeatherSnapshot(15.0, 0.0, WeatherType.CLEAR);
                    updateWeatherDisplay(fallback);
                    computeSpeed(fallback);
                }
            });
        }
    }

    private void updateWeatherDisplay(WeatherSnapshot weather) {
        binding.textTemp.setText(String.format(Locale.US, "Temperature: %.1f °C", weather.getTemperatureCelsius()));
        binding.textPrecip.setText(String.format(Locale.US, "Precipitation: %.1f mm", weather.getPrecipitationMm()));
    }

    private void computeSpeed(WeatherSnapshot weather) {
        RoadType roadType = binding.spinnerRoad.getSelectedItemPosition() == 0
                ? RoadType.ASPHALT
                : RoadType.GRAVEL;

        TimeOfDay timeOfDay = binding.radioDay.isChecked()
                ? TimeOfDay.DAY
                : TimeOfDay.NIGHT;

        int minSpeed = settingsStore.getMin();
        int maxSpeed = settingsStore.getMax();
        int baseSpeed = settingsStore.getBase();

        DriverSettings settings = new DriverSettings.Builder()
                .withRoadType(roadType)
                .withTimeOfDay(timeOfDay)
                .withMinAllowedSpeed(minSpeed)
                .withMaxAllowedSpeed(maxSpeed)
                .withBaseSpeed(baseSpeed)
                .build();

        SpeedDecision decision = ruleEngine.computeMaxSpeed(settings, weather);

        updateSpeedDisplay(decision);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            binding.buttonRecalculate.setEnabled(true);
        }, 600);
    }

    private void updateSpeedDisplay(SpeedDecision decision) {
        int currentSpeed = 0;
        try {
            String currentText = binding.textSpeedValue.getText().toString();
            if (!currentText.equals("--")) {
                currentSpeed = Integer.parseInt(currentText);
            }
        } catch (NumberFormatException e) {
            currentSpeed = 0;
        }

        int newSpeed = decision.getMaxSpeedKmh();

        binding.textSpeedValue.setText(String.valueOf(newSpeed));

        ObjectAnimator animator = ObjectAnimator.ofInt(
                binding.progressSpeed,
                "progress",
                currentSpeed,
                newSpeed
        );
        animator.setDuration(600);
        animator.start();

        binding.textDebug.setText(decision.getReason());

        Log.d(TAG, "Speed computed: " + newSpeed + " km/h - " + decision.getReason());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
