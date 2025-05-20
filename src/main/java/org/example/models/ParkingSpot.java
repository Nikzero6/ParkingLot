package org.example.models;

import org.example.models.enums.ParkingSpotStatus;
import org.example.models.enums.VehicleType;

public class ParkingSpot {
    private final String id;
    private final VehicleType vehicleType;
    private ParkingSpotStatus parkingSpotStatus;

    public ParkingSpot(String id, VehicleType vehicleType, ParkingSpotStatus parkingSpotStatus) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.parkingSpotStatus = parkingSpotStatus;
    }

    public String getId() {
        return id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public ParkingSpotStatus getParkingSpotStatus() {
        return parkingSpotStatus;
    }

    public void setParkingSpotStatus(ParkingSpotStatus parkingSpotStatus) {
        this.parkingSpotStatus = parkingSpotStatus;
    }

    public Boolean isAvailable() {
        return parkingSpotStatus == ParkingSpotStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return "ParkingSpot{" + "id='" + id + '\'' + ", vehicleType=" + vehicleType + ", parkingSpotStatus=" + parkingSpotStatus + '}';
    }
}
