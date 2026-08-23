package com.flightops.platform.event;

import com.flightops.platform.domain.FlightEventType;
import com.flightops.platform.domain.FlightStatus;
import java.time.LocalDateTime;

public class FlightEvent {

    private FlightEventType eventType;
    private Long flightId;
    private String flightNumber;
    private String origin;
    private String destination;
    private FlightStatus status;
    private Integer delayMinutes;
    private LocalDateTime occurredAt;

    public FlightEvent() {
    }

    public FlightEvent(FlightEventType eventType, Long flightId, String flightNumber, String origin,
            String destination, FlightStatus status, Integer delayMinutes, LocalDateTime occurredAt) {
        this.eventType = eventType;
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.status = status;
        this.delayMinutes = delayMinutes;
        this.occurredAt = occurredAt;
    }

    public FlightEventType getEventType() {
        return eventType;
    }

    public void setEventType(FlightEventType eventType) {
        this.eventType = eventType;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public Integer getDelayMinutes() {
        return delayMinutes;
    }

    public void setDelayMinutes(Integer delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }
}
