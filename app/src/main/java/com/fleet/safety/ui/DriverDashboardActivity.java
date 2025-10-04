package com.fleet.safety.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fleet.safety.R;
import com.fleet.safety.data.remote.OpenMeteoWeatherService;
import com.fleet.safety.data.remote.WeatherCallback;
import com.fleet.safety.domain.DriverSettings;
import com.fleet.safety.domain.RoadType;
import com.fleet.safety.domain.SpeedDecision;
import com.fleet.safety.domain.SpeedRuleEngine;
import com.fleet.safety.domain.TimeOfDay;
import com.fleet.safety.domain.WeatherSnapshot;
import com.fleet.safety.domain.WeatherType;

public class DriverDashboardActivity extends AppCompatActivity {

    private TextView textTitle;
    private TextView textSubtitle;
    private TextView textMaxSpeed;

    private RadioGroup radioRoadType;
    private RadioButton radioAsphalt;
    private RadioButton radioGravel;
    private RadioGroup radioTimeOfDay;
    private RadioButton radioDay;
    private RadioButton radioNight;

    private TextView textTemperature;
    private TextView textPrecipitation;
    private Switch switchOfflineWeather;

    private Button buttonRecalculate;

    private OpenMeteoWeatherService weatherService;
    private SpeedRuleEngine speedRuleEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

        setTitle(R.string.driver_dashboard_title);

        initializeComponents();
        initializeViews();
        setupListeners();
    }

    private void initializeComponents() {
        weatherService = new OpenMeteoWeatherService();
        speedRuleEngine = new SpeedRuleEngine();
    }

    private void initializeViews() {
        textTitle = findViewById(R.id.textTitle);
        textSubtitle = findViewById(R.id.textSubtitle);

        textMaxSpeed = findViewById(R.id.textMaxSpeed);

        radioRoadType = findViewById(R.id.radioRoadType);
        radioAsphalt = findViewById(R.id.radioAsphalt);
        radioGravel = findViewById(R.id.radioGravel);

        radioTimeOfDay = findViewById(R.id.radioTimeOfDay);
        radioDay = findViewById(R.id.radioDay);
        radioNight = findViewById(R.id.radioNight);

        textTemperature = findViewById(R.id.textTemperature);
        textPrecipitation = findViewById(R.id.textPrecipitation);
        switchOfflineWeather = findViewById(R.id.switchOfflineWeather);

        buttonRecalculate = findViewById(R.id.buttonRecalculate);
    }

    private void setupListeners() {
        buttonRecalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverSettings settings = readSettingsFromUi();
                requestWeatherAndCompute(settings);
            }
        });
    }

    /**
     * Reads the current UI state and builds a DriverSettings object.
     *
     * @return DriverSettings based on current UI selections
     */
    private DriverSettings readSettingsFromUi() {
        RoadType roadType = radioAsphalt.isChecked() ? RoadType.ASPHALT : RoadType.GRAVEL;

        TimeOfDay timeOfDay = radioDay.isChecked() ? TimeOfDay.DAY : TimeOfDay.NIGHT;

        return DriverSettings.builder()
                .withRoadType(roadType)
                .withTimeOfDay(timeOfDay)
                .build();
    }

    /**
     * Requests weather data and computes maximum speed.
     * Uses offline mode if switch is enabled, otherwise fetches live weather data.
     *
     * @param settings The driver settings to use for calculation
     */
    private void requestWeatherAndCompute(DriverSettings settings) {
        setLoadingState(true);

        if (switchOfflineWeather.isChecked()) {
            WeatherSnapshot offlineWeather = new WeatherSnapshot(5.0, 0.0, WeatherType.CLEAR);
            processWeatherAndCompute(settings, offlineWeather);
        } else {
            weatherService.getCurrentAsyncForComodoro(new WeatherCallback() {
                @Override
                public void onSuccess(WeatherSnapshot snapshot) {
                    processWeatherAndCompute(settings, snapshot);
                }

                @Override
                public void onError(Exception e) {
                    setLoadingState(false);
                    Toast.makeText(DriverDashboardActivity.this,
                            "Weather service error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Processes weather data and computes the maximum safe speed.
     *
     * @param settings The driver settings
     * @param weatherSnapshot The weather data
     */
    private void processWeatherAndCompute(DriverSettings settings, WeatherSnapshot weatherSnapshot) {
        updateWeatherDisplay(weatherSnapshot);

        SpeedDecision decision = speedRuleEngine.computeMaxSpeed(settings, weatherSnapshot);

        updateSpeedDisplay(decision);

        setLoadingState(false);
    }

    /**
     * Updates the weather information display.
     *
     * @param weatherSnapshot The weather data to display
     */
    private void updateWeatherDisplay(WeatherSnapshot weatherSnapshot) {
        textTemperature.setText(String.format("%.1f °C", weatherSnapshot.getTemperatureCelsius()));
        textPrecipitation.setText(String.format("%.1f mm", weatherSnapshot.getPrecipitationMm()));
    }

    /**
     * Updates the maximum speed display.
     *
     * @param decision The speed decision containing the computed max speed
     */
    private void updateSpeedDisplay(SpeedDecision decision) {
        textMaxSpeed.setText(String.format("%d km/h", decision.getMaxSpeedKmh()));
    }

    /**
     * Sets the loading state of the UI.
     *
     * @param loading true to show loading state, false to restore normal state
     */
    private void setLoadingState(boolean loading) {
        if (loading) {
            textMaxSpeed.setText("-- km/h");
            buttonRecalculate.setEnabled(false);
        } else {
            buttonRecalculate.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (weatherService != null) {
            weatherService.shutdown();
        }
    }
}
