package org.example;

import org.example.models.Gate;
import org.example.models.ParkingLot;
import org.example.models.ParkingTicket;
import org.example.models.Vehicle;
import org.example.models.enums.GateType;
import org.example.scripts.ParkingLotScripts;
import org.example.services.ParkingLotService;
import org.example.services.ParkingTicketService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ParkingLotService parkingLotService = ParkingLotService.getInstance();
        ParkingTicketService parkingTicketService = ParkingTicketService.getInstance();

        ParkingLot parkingLot = parkingLotService.getParkingLot();
        List<Vehicle> vehicles = ParkingLotScripts.generateVehicles();

        parkingLotService.displayParkingLotStatus();

        // park all vehicles
        for (Vehicle vehicle : vehicles) {
            Gate gate = ParkingLotScripts.getRandomGate(parkingLot.getGates(), GateType.ENTRY);
            parkingLotService.parkVehicle(vehicle, gate.id());
        }

        parkingLotService.displayParkingLotStatus();

        // unPark all vehicles
        for (ParkingTicket parkingTicket : parkingTicketService.getParkingTickets()) {
            Gate gate = ParkingLotScripts.getRandomGate(parkingLot.getGates(), GateType.EXIT);
            parkingLotService.unParkVehicle(parkingTicket, gate.id());
        }

        parkingLotService.displayParkingLotStatus();
    }
}