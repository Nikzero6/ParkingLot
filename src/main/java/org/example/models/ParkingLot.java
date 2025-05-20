package org.example.models;

import org.example.models.enums.ParkingSpotStatus;
import org.example.models.enums.VehicleType;
import org.example.strategy.ParkingFloor.ParkingFloorStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ParkingLot {
    private final String id;
    private final Map<String, Gate> gates;
    private final Map<String, ParkingFloor> floors;
    private final ParkingFloorStrategy parkingFloorStrategy;

    public ParkingLot(String id, ParkingFloorStrategy parkingFloorStrategy) {
        this.id = id;
        this.gates = new HashMap<>();
        this.floors = new HashMap<>();
        this.parkingFloorStrategy = parkingFloorStrategy;
    }

    public String getId() {
        return id;
    }

    public List<Gate> getGates() {
        return gates.values().stream().toList();
    }

    public Gate getGate(String gateId) {
        return gates.get(gateId);
    }

    public List<ParkingFloor> getFloors() {
        return floors.values().stream().toList();
    }

    public void addGate(Gate gate) {
        gates.put(gate.id(), gate);
    }

    public void addGates(List<Gate> gates) {
        for (Gate gate : gates) {
            addGate(gate);
        }
    }

    public void addFloor(ParkingFloor parkingFloor) {
        floors.put(parkingFloor.getId(), parkingFloor);
    }

    public void addFloors(List<ParkingFloor> parkingFloors) {
        for (ParkingFloor parkingFloor : parkingFloors) {
            addFloor(parkingFloor);
        }
    }

    public Optional<ParkingFloor> findParkingFloor(VehicleType vehicleType, Gate gate) {
        return parkingFloorStrategy.assignFloor(vehicleType, gate, getFloors());
    }

    public void displayParkingLotStatus() {
        System.out.println("===== ParkingLotStatus =====\n");

        for (VehicleType vehicleType : VehicleType.values()) {
            System.out.println(vehicleType + ":\n");

            for (ParkingSpotStatus parkingSpotStatus : ParkingSpotStatus.values()) {
                System.out.println(parkingSpotStatus + ":");

                for (ParkingFloor floor : getFloors()) {
                    System.out.println(
                            floor.getId() + " : " + floor.getParkingSpotsCount(
                                    vehicleType,
                                    parkingSpotStatus
                            ));
                }
            }
        }

        System.out.println("=======================\n");
    }

    @Override
    public String toString() {
        return "ParkingLot{" + "\n" + "id='" + id + '\'' + "\n" + "gates=" + gates + "\n" + "floors=" + floors + "\n" + '}';
    }
}
