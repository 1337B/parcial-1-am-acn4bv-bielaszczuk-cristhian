package com.fleet.safety.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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

    private TextView badgeReason;

    private Integer adminMinSpeed = null;
    private Integer adminMaxSpeed = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

        setTitle(R.string.driver_dashboard_title);

        initializeComponents();
        initializeViews();
        readIntentExtras();
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

        createSpeedBadge();
    }

    private void readIntentExtras() {
        Intent intent = getIntent();
        if (intent != null) {
            String roadTypeExtra = intent.getStringExtra("roadType");
            if (roadTypeExtra != null) {
                if ("ASPHALT".equals(roadTypeExtra)) {
                    radioAsphalt.setChecked(true);
                } else if ("GRAVEL".equals(roadTypeExtra)) {
                    radioGravel.setChecked(true);
                }
            }

            String timeOfDayExtra = intent.getStringExtra("timeOfDay");
            if (timeOfDayExtra != null) {
                if ("DAY".equals(timeOfDayExtra)) {
                    radioDay.setChecked(true);
                } else if ("NIGHT".equals(timeOfDayExtra)) {
                    radioNight.setChecked(true);
                }
            }

            if (intent.hasExtra("minAllowedSpeed")) {
                adminMinSpeed = intent.getIntExtra("minAllowedSpeed", 0);
            }

            if (intent.hasExtra("maxAllowedSpeed")) {
                adminMaxSpeed = intent.getIntExtra("maxAllowedSpeed", 0);
            }
        }
    }

    private void createSpeedBadge() {
        ConstraintLayout rootLayout = (ConstraintLayout) findViewById(android.R.id.content).getRootView()
                .findViewById(R.id.textMaxSpeed).getParent();

        badgeReason = new TextView(this);
        badgeReason.setId(View.generateViewId());
        badgeReason.setText("");
        badgeReason.setBackgroundResource(R.drawable.speed_badge);
        badgeReason.setTextSize(12f);
        badgeReason.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        badgeReason.setVisibility(View.GONE);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );

        int marginSpace = getResources().getDimensionPixelSize(R.dimen.space_s);
        layoutParams.topMargin = marginSpace;
        layoutParams.leftMargin = marginSpace;
        layoutParams.rightMargin = marginSpace;

        badgeReason.setLayoutParams(layoutParams);

        rootLayout.addView(badgeReason);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(rootLayout);

        constraintSet.connect(badgeReason.getId(), ConstraintSet.TOP,
                R.id.textMaxSpeed, ConstraintSet.BOTTOM, marginSpace);
        constraintSet.connect(badgeReason.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(badgeReason.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);

        constraintSet.applyTo(rootLayout);
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
     * Reads the current UI state and builds a DriverSettings object including admin bounds.
     *
     * @return DriverSettings based on current UI selections and admin bounds
     */
    private DriverSettings readSettingsFromUi() {
        RoadType roadType = radioAsphalt.isChecked() ? RoadType.ASPHALT : RoadType.GRAVEL;

        TimeOfDay timeOfDay = radioDay.isChecked() ? TimeOfDay.DAY : TimeOfDay.NIGHT;

        DriverSettings.Builder builder = DriverSettings.builder()
                .withRoadType(roadType)
                .withTimeOfDay(timeOfDay);

        if (adminMinSpeed != null) {
            builder.withMinAllowedSpeed(adminMinSpeed);
        }

        if (adminMaxSpeed != null) {
            builder.withMaxAllowedSpeed(adminMaxSpeed);
        }

        return builder.build();
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
     * Updates the maximum speed display and badge.
     *
     * @param decision The speed decision containing the computed max speed and reasoning
     */
    private void updateSpeedDisplay(SpeedDecision decision) {
        textMaxSpeed.setText(String.format("%d km/h", decision.getMaxSpeedKmh()));

        badgeReason.setText(decision.getReason());
        badgeReason.setVisibility(View.VISIBLE);
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
