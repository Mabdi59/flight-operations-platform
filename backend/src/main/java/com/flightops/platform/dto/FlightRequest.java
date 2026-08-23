package com.flightops.platform.dto;

import com.flightops.platform.domain.FlightStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record FlightRequest(
        @NotBlank String flightNumber,
        @NotBlank String origin,
        @NotBlank String destination,
        @NotNull LocalDateTime scheduledDeparture,
        @NotNull LocalDateTime scheduledArrival,
        LocalDateTime actualDeparture,
        LocalDateTime actualArrival,
        @NotNull FlightStatus status,
        @Min(0) Integer delayMinutes) {
}
