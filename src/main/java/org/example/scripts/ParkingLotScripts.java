package org.example.scripts;

import org.example.models.*;
import org.example.models.enums.GateType;
import org.example.models.enums.ParkingSpotStatus;
import org.example.models.enums.VehicleType;
import org.example.strategy.ParkingFloor.NearestParkingFloorStrategy;
import org.example.strategy.ParkingSpot.NearestParkingSpotStrategy;

import java.util.ArrayList;
import java.util.List;

public class ParkingLotScripts {
    private static int parkingLotsCount = 0;
    private static int vehiclesCount = 0;
    private static int gatesCount = 0;
    private static int parkingFloorsCount = 0;
    private static int parkingSpotsCount = 0;

    public static ParkingLot generateParkingLot() {
        String parkingLotId = getParkingLotId();

        ParkingLot parkingLot = new ParkingLot(parkingLotId, new NearestParkingFloorStrategy());
        parkingLot.addGates(generateGates());
        parkingLot.addFloors(generateParkingFloors());

        return parkingLot;
    }

    public static List<Vehicle> generateVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String vehicleNumber = getVehicleNumber();
            VehicleType vehicleType = getVehicleType();

            vehicles.add(new Vehicle(vehicleNumber, vehicleType));
        }

        return vehicles;
    }

    public static Gate getRandomGate(List<Gate> gates, GateType gateType) {
        List<Gate> filteredGates = gates.stream().filter(gate -> gate.type() == gateType).toList();

        int randomIndex = (int) Math.floor(Math.random() * filteredGates.size());

        return filteredGates.get(randomIndex);
    }

    private static String getVehicleNumber() {
        vehiclesCount++;
        return "Vehicle-" + vehiclesCount;
    }

    private static VehicleType getVehicleType() {
        VehicleType[] vehicleTypes = VehicleType.values();

        int randomIndex = (int) Math.floor(Math.random() * (vehicleTypes.length));

        return vehicleTypes[randomIndex];
    }

    private static String getParkingLotId() {
        parkingLotsCount++;
        return "ParkingLot-" + parkingLotsCount;
    }

    private static List<Gate> generateGates() {
        List<Gate> gates = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            gates.add(generateGate());
        }

        return gates;
    }

    private static Gate generateGate() {
        gatesCount++;

        String gateId = "Gate-" + gatesCount;
        GateType gateType = Math.random() < 0.5 ? GateType.ENTRY : GateType.EXIT;

        return new Gate(gateId, gatesCount, gateType);
    }

    private static List<ParkingFloor> generateParkingFloors() {
        List<ParkingFloor> parkingFloors = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            parkingFloors.add(generateParkingFloor());
        }

        return parkingFloors;
    }

    private static ParkingFloor generateParkingFloor() {
        parkingFloorsCount++;

        String parkingFloorId = "ParkingFloor-" + parkingFloorsCount;

        ParkingFloor parkingFloor = new ParkingFloor(
                parkingFloorId, new NearestParkingSpotStrategy());

        parkingFloor.addParkingSpots(generateParkingSpots());

        return parkingFloor;
    }

    private static List<ParkingSpot> generateParkingSpots() {
        List<ParkingSpot> parkingSpots = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            parkingSpots.add(generateParkingSpot());
        }

        return parkingSpots;
    }

    private static ParkingSpot generateParkingSpot() {
        parkingSpotsCount++;

        String parkingSpotId = "ParkingSpot-" + parkingSpotsCount;

        double randomNum = Math.random();

        VehicleType vehicleType = randomNum < 0.3 ? VehicleType.TWO_WHEELER : randomNum < 0.8 ? VehicleType.LMV : VehicleType.HMV;

        randomNum = Math.random();

        ParkingSpotStatus parkingSpotStatus = randomNum < 0.1 ? ParkingSpotStatus.INACTIVE : randomNum < 0.3 ? ParkingSpotStatus.OCCUPIED : ParkingSpotStatus.AVAILABLE;

        return new ParkingSpot(parkingSpotId, vehicleType, parkingSpotStatus);
    }
}
