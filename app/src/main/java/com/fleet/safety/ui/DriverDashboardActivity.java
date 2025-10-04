package com.fleet.safety.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fleet.safety.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

        setTitle(R.string.driver_dashboard_title);

        initializeViews();
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
}
