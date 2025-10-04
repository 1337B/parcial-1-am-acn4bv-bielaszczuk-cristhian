package com.fleet.safety.domain;
public class SpeedDecision {
    private final int maxSpeedKmh;
    private final String reason;

    public SpeedDecision(int maxSpeedKmh, String reason) {
        this.maxSpeedKmh = maxSpeedKmh;
        this.reason = reason;
    }

    public int getMaxSpeedKmh() {
        return maxSpeedKmh;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "SpeedDecision{" +
                "maxSpeedKmh=" + maxSpeedKmh +
                ", reason='" + reason + '\'' +
                '}';
    }
}
