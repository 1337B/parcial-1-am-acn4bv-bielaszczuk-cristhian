package com.fleet.safety.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fleet.safety.R;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton buttonGoDriver = findViewById(R.id.button_go_driver);
        MaterialButton buttonGoAdmin = findViewById(R.id.button_go_admin);

        buttonGoDriver.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this, DriverDashboardActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error starting DriverDashboardActivity", e);
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        buttonGoAdmin.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this, AdminSettingsActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error starting AdminSettingsActivity", e);
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
