package org.example.models;

import org.example.models.enums.GateType;

public record Gate(String id, int number, GateType type) {
}
