package com.fleet.safety.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fleet.safety.R;
import com.fleet.safety.databinding.ActivityAdminSettingsBinding;

public class AdminSettingsActivity extends AppCompatActivity {

    private ActivityAdminSettingsBinding binding;

    private TextView textTitleAdmin;
    private RadioGroup radioRoadTypeAdmin;
    private RadioButton radioAsphaltAdmin;
    private RadioButton radioGravelAdmin;
    private RadioGroup radioTimeOfDayAdmin;
    private RadioButton radioDayAdmin;
    private RadioButton radioNightAdmin;
    private EditText editMinSpeed;
    private EditText editMaxSpeed;
    private Button buttonSaveSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(R.string.admin_settings_title);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        textTitleAdmin = binding.textTitleAdmin;

        radioRoadTypeAdmin = binding.radioRoadTypeAdmin;
        radioAsphaltAdmin = binding.radioAsphaltAdmin;
        radioGravelAdmin = binding.radioGravelAdmin;

        radioTimeOfDayAdmin = binding.radioTimeOfDayAdmin;
        radioDayAdmin = binding.radioDayAdmin;
        radioNightAdmin = binding.radioNightAdmin;

        editMinSpeed = binding.editMinSpeed;
        editMaxSpeed = binding.editMaxSpeed;

        buttonSaveSettings = binding.buttonSaveSettings;
    }

    private void setupListeners() {
        buttonSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettingsAndNavigateToDriver();
            }
        });
    }

    private void saveSettingsAndNavigateToDriver() {
        String roadType = radioAsphaltAdmin.isChecked() ? "ASPHALT" : "GRAVEL";

        String timeOfDay = radioDayAdmin.isChecked() ? "DAY" : "NIGHT";

        Intent intent = new Intent(this, DriverDashboardActivity.class);

        intent.putExtra("roadType", roadType);
        intent.putExtra("timeOfDay", timeOfDay);

        String minSpeedText = editMinSpeed.getText().toString().trim();
        if (!TextUtils.isEmpty(minSpeedText)) {
            try {
                int minSpeed = Integer.parseInt(minSpeedText);
                intent.putExtra("minAllowedSpeed", minSpeed);
            } catch (NumberFormatException e) {
            }
        }

        String maxSpeedText = editMaxSpeed.getText().toString().trim();
        if (!TextUtils.isEmpty(maxSpeedText)) {
            try {
                int maxSpeed = Integer.parseInt(maxSpeedText);
                intent.putExtra("maxAllowedSpeed", maxSpeed);
            } catch (NumberFormatException e) {
            }
        }

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
