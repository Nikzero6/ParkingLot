package org.example.strategy.ParkingSpot;

import org.example.models.ParkingSpot;
import org.example.models.enums.VehicleType;

import java.util.List;
import java.util.Optional;

public interface ParkingSpotStrategy {
    Optional<ParkingSpot> findSpot(VehicleType vehicleType, List<ParkingSpot> parkingSpots);
}
