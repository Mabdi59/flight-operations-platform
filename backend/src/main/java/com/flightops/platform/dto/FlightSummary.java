package com.flightops.platform.dto;

import com.flightops.platform.domain.FlightStatus;
import java.time.LocalDateTime;

public record FlightSummary(
        Long id,
        String flightNumber,
        String origin,
        String destination,
        LocalDateTime scheduledDeparture,
        LocalDateTime scheduledArrival,
        FlightStatus status,
        Integer delayMinutes) {
}
