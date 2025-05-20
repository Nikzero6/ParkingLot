package org.example.strategy.ParkingSpot;

import org.example.models.ParkingSpot;
import org.example.models.enums.VehicleType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NearestParkingSpotStrategy implements ParkingSpotStrategy {
    @Override
    public Optional<ParkingSpot> findSpot(VehicleType vehicleType, List<ParkingSpot> parkingSpots) {
        List<ParkingSpot> availableParkingSpots = new ArrayList<>(parkingSpots.stream().filter(
                parkingSpot -> parkingSpot.isAvailable() && parkingSpot.getVehicleType() == vehicleType).toList());

        ParkingSpot parkingSpot = null;

        availableParkingSpots.sort(Comparator.comparing(ParkingSpot::getId));

        if (!availableParkingSpots.isEmpty()) {
            parkingSpot = availableParkingSpots.getFirst();
        }

        return Optional.ofNullable(parkingSpot);
    }
}
