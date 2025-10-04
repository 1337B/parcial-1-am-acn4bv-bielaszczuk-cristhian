package com.fleet.safety.domain;

public class DriverSettings {
    private final RoadType roadType;
    private final TimeOfDay timeOfDay;
    private final Integer minAllowedSpeed;
    private final Integer maxAllowedSpeed;

    private DriverSettings(RoadType roadType, TimeOfDay timeOfDay, Integer minAllowedSpeed, Integer maxAllowedSpeed) {
        this.roadType = roadType;
        this.timeOfDay = timeOfDay;
        this.minAllowedSpeed = minAllowedSpeed;
        this.maxAllowedSpeed = maxAllowedSpeed;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RoadType roadType;
        private TimeOfDay timeOfDay;
        private Integer minAllowedSpeed;
        private Integer maxAllowedSpeed;

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

        public DriverSettings build() {
            return new DriverSettings(roadType, timeOfDay, minAllowedSpeed, maxAllowedSpeed);
        }
    }
}
