package com.fleet.safety.domain;

public class DriverSettings {
    private final RoadType roadType;
    private final TimeOfDay timeOfDay;
    private final Integer minAllowedSpeed;
    private final Integer maxAllowedSpeed;

    private final Integer baseSpeed;

    private DriverSettings(RoadType roadType, TimeOfDay timeOfDay, Integer minAllowedSpeed, Integer maxAllowedSpeed, Integer baseSpeed) {
        this.roadType = roadType;
        this.timeOfDay = timeOfDay;
        this.minAllowedSpeed = minAllowedSpeed;
        this.maxAllowedSpeed = maxAllowedSpeed;
        this.baseSpeed = baseSpeed;
    }

    public RoadType getRoadType() {
        return roadType;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public Integer getMinAllowedSpeed() {
        return minAllowedSpeed;
    }

    public Integer getMaxAllowedSpeed() {
        return maxAllowedSpeed;
    }

    public Integer getBaseSpeed() {
        return baseSpeed;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RoadType roadType;
        private TimeOfDay timeOfDay;
        private Integer minAllowedSpeed;
        private Integer maxAllowedSpeed;
        private Integer baseSpeed;

        public Builder withRoadType(RoadType roadType) {
            this.roadType = roadType;
            return this;
        }

        public Builder withTimeOfDay(TimeOfDay timeOfDay) {
            this.timeOfDay = timeOfDay;
            return this;
        }

        public Builder withMinAllowedSpeed(Integer minAllowedSpeed) {
            this.minAllowedSpeed = minAllowedSpeed;
            return this;
        }

        public Builder withMaxAllowedSpeed(Integer maxAllowedSpeed) {
            this.maxAllowedSpeed = maxAllowedSpeed;
            return this;
        }

        public Builder withBaseSpeed(Integer baseSpeed) {
            this.baseSpeed = baseSpeed;
            return this;
        }

        public DriverSettings build() {
            return new DriverSettings(roadType, timeOfDay, minAllowedSpeed, maxAllowedSpeed, baseSpeed);
        }
    }
}
