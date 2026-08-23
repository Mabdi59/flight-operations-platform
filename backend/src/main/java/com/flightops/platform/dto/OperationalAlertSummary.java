package com.flightops.platform.dto;

import com.flightops.platform.domain.AlertSeverity;
import com.flightops.platform.domain.FlightEventType;
import java.time.LocalDateTime;

public record OperationalAlertSummary(
        Long id,
        FlightEventType eventType,
        AlertSeverity severity,
        String message,
        LocalDateTime createdAt) {
}
