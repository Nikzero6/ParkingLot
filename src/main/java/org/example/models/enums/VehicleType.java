package org.example.models.enums;

public enum VehicleType {
    TWO_WHEELER(20.0, 10.0),
    LMV(35.0, 15.0),
    HMV(50.0, 20.0);

    private final double baseCharge;
    private final double hourlyRate;

    VehicleType(double baseCharge, double hourlyRate) {
        this.baseCharge = baseCharge;
        this.hourlyRate = hourlyRate;
    }

    public double getBaseCharge() {
        return baseCharge;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}
