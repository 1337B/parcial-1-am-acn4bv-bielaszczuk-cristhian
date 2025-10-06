package com.fleet.safety.domain;

import java.util.ArrayList;
import java.util.List;
public class SpeedRuleEngine {

    private static final int DEFAULT_BASE_SPEED = 80;
    private static final int GLOBAL_MIN_SPEED = 20;
    private static final int GLOBAL_MAX_SPEED = 110;
    private static final int GRAVEL_PENALTY = 20;
    private static final int NIGHT_PENALTY = 10;
    private static final int RAIN_PENALTY = 10;
    private static final int SNOW_PENALTY = 20;
    private static final int ICE_PENALTY = 30;

    /**
     * Computes the maximum safe speed based on driver settings and current weather conditions.
     *
     * @param settings Driver settings including road type, time of day, and optional speed bounds.
     *                 Must not be null and must have non-null roadType and timeOfDay.
     * @param weather  Current weather snapshot. If null, defaults to clear weather conditions.
     * @return SpeedDecision containing the computed maximum speed and reasoning
     * @throws IllegalArgumentException if settings is null or has null roadType/timeOfDay
     */
    public SpeedDecision computeMaxSpeed(DriverSettings settings, WeatherSnapshot weather) {
        validateSettings(settings);

        if (weather == null) {
            weather = new WeatherSnapshot(20.0, 0.0, WeatherType.CLEAR);
        }

        List<String> reasonParts = new ArrayList<>();

        int baseSpeed = settings.getBaseSpeed() != null ? settings.getBaseSpeed() : DEFAULT_BASE_SPEED;
        int currentSpeed = baseSpeed;
        reasonParts.add("base " + baseSpeed);

        if (settings.getRoadType() == RoadType.GRAVEL) {
            currentSpeed -= GRAVEL_PENALTY;
            reasonParts.add("-" + GRAVEL_PENALTY + " gravel");
        }

        if (settings.getTimeOfDay() == TimeOfDay.NIGHT) {
            currentSpeed -= NIGHT_PENALTY;
            reasonParts.add("-" + NIGHT_PENALTY + " night");
        }

        switch (weather.getWeatherType()) {
            case RAIN:
                currentSpeed -= RAIN_PENALTY;
                reasonParts.add("-" + RAIN_PENALTY + " rain");
                break;
            case SNOW:
                currentSpeed -= SNOW_PENALTY;
                reasonParts.add("-" + SNOW_PENALTY + " snow");
                break;
            case ICE:
                currentSpeed -= ICE_PENALTY;
                reasonParts.add("-" + ICE_PENALTY + " ice");
                break;
            case CLEAR:
                break;
        }

        boolean wasGlobalClamped = false;
        if (currentSpeed < GLOBAL_MIN_SPEED) {
            currentSpeed = GLOBAL_MIN_SPEED;
            wasGlobalClamped = true;
        } else if (currentSpeed > GLOBAL_MAX_SPEED) {
            currentSpeed = GLOBAL_MAX_SPEED;
            wasGlobalClamped = true;
        }

        if (wasGlobalClamped) {
            reasonParts.add("→ clamp " + currentSpeed);
        }

        boolean adminBoundsApplied = false;
        String adminBoundsText = "";

        if (settings.getMinAllowedSpeed() != null || settings.getMaxAllowedSpeed() != null) {
            Integer minBound = settings.getMinAllowedSpeed();
            Integer maxBound = settings.getMaxAllowedSpeed();

            if (minBound != null && currentSpeed < minBound) {
                currentSpeed = minBound;
                adminBoundsApplied = true;
            }

            if (maxBound != null && currentSpeed > maxBound) {
                currentSpeed = maxBound;
                adminBoundsApplied = true;
            }

            StringBuilder boundsDesc = new StringBuilder("admin bounds [");
            if (minBound != null) {
                boundsDesc.append("min=").append(minBound);
            }
            if (minBound != null && maxBound != null) {
                boundsDesc.append(", ");
            }
            if (maxBound != null) {
                boundsDesc.append("max=").append(maxBound);
            }
            boundsDesc.append("]");
            adminBoundsText = boundsDesc.toString();
        }

        StringBuilder finalReason = new StringBuilder();
        finalReason.append(String.join(", ", reasonParts));

        if (adminBoundsApplied) {
            finalReason.append(" → ").append(adminBoundsText).append(" → final ").append(currentSpeed);
        }

        return new SpeedDecision(currentSpeed, finalReason.toString());
    }

    private void validateSettings(DriverSettings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("Driver settings cannot be null");
        }

        if (settings.getRoadType() == null) {
            throw new IllegalArgumentException("Road type cannot be null in driver settings");
        }

        if (settings.getTimeOfDay() == null) {
            throw new IllegalArgumentException("Time of day cannot be null in driver settings");
        }
    }
}
