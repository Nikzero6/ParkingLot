package org.example.models;

import org.example.models.enums.VehicleType;

public record Vehicle(String regNumber, VehicleType type) {
}
