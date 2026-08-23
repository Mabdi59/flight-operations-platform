package com.flightops.platform.dto;

import java.time.LocalDateTime;

public record AircraftAssignmentSummary(
        Long assignmentId,
        String flightNumber,
        String aircraftTailNumber,
        String aircraftModel,
        LocalDateTime assignedAt) {
}
