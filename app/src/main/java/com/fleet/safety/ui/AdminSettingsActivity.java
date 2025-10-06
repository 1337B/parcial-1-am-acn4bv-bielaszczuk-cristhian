package com.fleet.safety.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fleet.safety.databinding.ActivityAdminSettingsBinding;

public class AdminSettingsActivity extends AppCompatActivity {

    private ActivityAdminSettingsBinding binding;
    private SettingsStore settingsStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsStore = new SettingsStore(this);

        loadSettings();

        binding.buttonSave.setOnClickListener(v -> saveSettings());
    }

    private void loadSettings() {
        binding.inputMinSpeed.setText(String.valueOf(settingsStore.getMin()));
        binding.inputMaxSpeed.setText(String.valueOf(settingsStore.getMax()));
        binding.inputBaseSpeed.setText(String.valueOf(settingsStore.getBase()));
    }

    private void saveSettings() {
        try {
            String minStr = binding.inputMinSpeed.getText().toString().trim();
            String maxStr = binding.inputMaxSpeed.getText().toString().trim();
            String baseStr = binding.inputBaseSpeed.getText().toString().trim();

            if (minStr.isEmpty() || maxStr.isEmpty() || baseStr.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            int min = Integer.parseInt(minStr);
            int max = Integer.parseInt(maxStr);
            int base = Integer.parseInt(baseStr);

            if (min > max) {
                Toast.makeText(this, "Min speed must be <= max speed", Toast.LENGTH_SHORT).show();
                return;
            }

            settingsStore.save(min, max, base);
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
