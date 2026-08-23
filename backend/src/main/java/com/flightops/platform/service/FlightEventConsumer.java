package com.flightops.platform.service;

import com.flightops.platform.domain.AlertSeverity;
import com.flightops.platform.domain.FlightEventType;
import com.flightops.platform.domain.OperationalAlert;
import com.flightops.platform.event.FlightEvent;
import java.time.LocalDateTime;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FlightEventConsumer {

    private final OperationalAlertService operationalAlertService;

    public FlightEventConsumer(OperationalAlertService operationalAlertService) {
        this.operationalAlertService = operationalAlertService;
    }

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}", autoStartup = "${app.kafka.enabled:true}")
    public void handleFlightEvent(FlightEvent event) {
        OperationalAlert alert = new OperationalAlert();
        alert.setEventType(event.getEventType());
        alert.setSeverity(resolveSeverity(event.getEventType()));
        alert.setMessage(buildMessage(event));
        alert.setCreatedAt(LocalDateTime.now());
        alert.setAcknowledged(false);
        operationalAlertService.createAlert(alert);
    }

    private AlertSeverity resolveSeverity(FlightEventType eventType) {
        return switch (eventType) {
            case FLIGHT_CANCELLED -> AlertSeverity.CRITICAL;
            case FLIGHT_DELAYED -> AlertSeverity.WARNING;
            case FLIGHT_DEPARTED, FLIGHT_ARRIVED -> AlertSeverity.INFO;
        };
    }

    private String buildMessage(FlightEvent event) {
        return switch (event.getEventType()) {
            case FLIGHT_DELAYED -> event.getFlightNumber() + " delayed by " + event.getDelayMinutes()
                    + " minutes on route " + event.getOrigin() + " → " + event.getDestination();
            case FLIGHT_CANCELLED -> event.getFlightNumber() + " was cancelled for route " + event.getOrigin()
                    + " → " + event.getDestination();
            case FLIGHT_DEPARTED -> event.getFlightNumber() + " departed from " + event.getOrigin();
            case FLIGHT_ARRIVED -> event.getFlightNumber() + " arrived at " + event.getDestination();
        };
    }
}
