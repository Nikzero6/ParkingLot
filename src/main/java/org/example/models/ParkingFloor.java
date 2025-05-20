package org.example.models;

import org.example.models.enums.ParkingSpotStatus;
import org.example.models.enums.VehicleType;
import org.example.strategy.ParkingSpot.ParkingSpotStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ParkingFloor {
    private final String id;
    private final Map<String, ParkingSpot> parkingSpots;
    private final ParkingSpotStrategy parkingSpotStrategy;

    public ParkingFloor(String id, ParkingSpotStrategy parkingSpotStrategy) {
        this.id = id;
        this.parkingSpots = new HashMap<>();
        this.parkingSpotStrategy = parkingSpotStrategy;
    }

    public String getId() {
        return id;
    }

    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots.values().stream().toList();
    }

    public int getParkingSpotsCount(VehicleType vehicleType, ParkingSpotStatus parkingSpotStatus) {
        return getParkingSpots(vehicleType, parkingSpotStatus).size();
    }

    public void addParkingSpot(ParkingSpot parkingSpot) {
        parkingSpots.put(parkingSpot.getId(), parkingSpot);
    }

    public void addParkingSpots(List<ParkingSpot> parkingSpots) {
        for (ParkingSpot parkingSpot : parkingSpots) {
            addParkingSpot(parkingSpot);
        }
    }

    public Optional<ParkingSpot> findParkingSpot(VehicleType vehicleType) {
        return parkingSpotStrategy.findSpot(vehicleType, getParkingSpots());
    }

    @Override
    public String toString() {
        return "ParkingFloor{" + "\n" + "id='" + id + '\'' + "\n" + "parkingSpots=" + parkingSpots + "\n" + '}';
    }

    private List<ParkingSpot> getParkingSpots(VehicleType vehicleType, ParkingSpotStatus parkingSpotStatus) {
        return parkingSpots.values().stream().filter(
                parkingSpot -> parkingSpot.getVehicleType() == vehicleType && parkingSpot.getParkingSpotStatus() == parkingSpotStatus).toList();
    }
}
