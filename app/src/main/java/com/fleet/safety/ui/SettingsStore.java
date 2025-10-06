package com.fleet.safety.ui;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsStore {

    private static final String PREFS_NAME = "FleetSafetySettings";
    private static final String KEY_MIN_SPEED = "min_speed";
    private static final String KEY_MAX_SPEED = "max_speed";
    private static final String KEY_BASE_SPEED = "base_speed";

    private static final int DEFAULT_MIN = 60;
    private static final int DEFAULT_MAX = 120;
    private static final int DEFAULT_BASE = 80;

    private final SharedPreferences prefs;

    public SettingsStore(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getMin() {
        return prefs.getInt(KEY_MIN_SPEED, DEFAULT_MIN);
    }

    public int getMax() {
        return prefs.getInt(KEY_MAX_SPEED, DEFAULT_MAX);
    }

    public int getBase() {
        return prefs.getInt(KEY_BASE_SPEED, DEFAULT_BASE);
    }

    public void save(int min, int max, int base) {
        prefs.edit()
                .putInt(KEY_MIN_SPEED, min)
                .putInt(KEY_MAX_SPEED, max)
                .putInt(KEY_BASE_SPEED, base)
                .apply();
    }
}

