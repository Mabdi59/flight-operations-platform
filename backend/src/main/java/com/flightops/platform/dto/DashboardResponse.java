package com.flightops.platform.dto;

import java.util.List;

public record DashboardResponse(
        long totalActiveFlights,
        long delayedFlights,
        long cancelledFlights,
        long availableAircraft,
        List<FlightSummary> currentFlights,
        List<AircraftAssignmentSummary> aircraftAssignments,
        List<OperationalAlertSummary> operationalAlerts) {
}
