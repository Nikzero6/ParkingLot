package org.example.strategy.ParkingFloor;

import org.example.models.Gate;
import org.example.models.ParkingFloor;
import org.example.models.enums.ParkingSpotStatus;
import org.example.models.enums.VehicleType;

import java.util.List;
import java.util.Optional;

public class NearestParkingFloorStrategy implements ParkingFloorStrategy {
    @Override
    public Optional<ParkingFloor> assignFloor(VehicleType vehicleType, Gate gate, List<ParkingFloor> floors) {
        List<ParkingFloor> availableFloors = floors.stream().filter(
                floor -> floor.getParkingSpotsCount(vehicleType,
                                                    ParkingSpotStatus.AVAILABLE
                ) != 0).toList();

        if (availableFloors.isEmpty()) {
            return Optional.empty();
        }

        int floorIndex = gate.number() % floors.size();

        return Optional.ofNullable(floors.get(floorIndex));
    }
}
