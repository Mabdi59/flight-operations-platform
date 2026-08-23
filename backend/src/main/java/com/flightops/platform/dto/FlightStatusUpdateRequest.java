package com.flightops.platform.dto;

import com.flightops.platform.domain.FlightStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record FlightStatusUpdateRequest(
        @NotNull FlightStatus status,
        @Min(0) Integer delayMinutes,
        LocalDateTime actualDeparture,
        LocalDateTime actualArrival) {
}
