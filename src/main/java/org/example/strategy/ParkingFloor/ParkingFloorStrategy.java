package org.example.strategy.ParkingFloor;

import org.example.models.Gate;
import org.example.models.ParkingFloor;
import org.example.models.enums.VehicleType;

import java.util.List;
import java.util.Optional;

public interface ParkingFloorStrategy {
    Optional<ParkingFloor> assignFloor(VehicleType vehicleType, Gate gate, List<ParkingFloor> floors);
}
