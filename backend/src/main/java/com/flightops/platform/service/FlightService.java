package com.flightops.platform.service;

import com.flightops.platform.domain.Flight;
import com.flightops.platform.domain.FlightEventType;
import com.flightops.platform.domain.FlightStatus;
import com.flightops.platform.dto.FlightRequest;
import com.flightops.platform.dto.FlightStatusUpdateRequest;
import com.flightops.platform.event.FlightEvent;
import com.flightops.platform.repository.FlightRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightEventPublisher flightEventPublisher;

    public FlightService(FlightRepository flightRepository, FlightEventPublisher flightEventPublisher) {
        this.flightRepository = flightRepository;
        this.flightEventPublisher = flightEventPublisher;
    }

    @Transactional(readOnly = true)
    @Cacheable("flights")
    public List<Flight> getFlights() {
        return flightRepository.findAllByOrderByScheduledDepartureAsc();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "flight", key = "#id")
    public Flight getFlight(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight %d not found".formatted(id)));
    }

    @CacheEvict(cacheNames = {"flights", "flight", "dashboard"}, allEntries = true)
    public Flight createFlight(FlightRequest request) {
        Flight flight = new Flight();
        applyRequest(flight, request);
        return flightRepository.save(flight);
    }

    @CacheEvict(cacheNames = {"flights", "flight", "dashboard"}, allEntries = true)
    public Flight updateFlight(Long id, FlightRequest request) {
        Flight flight = findFlightOrThrow(id);
        FlightStatus previousStatus = flight.getStatus();
        applyRequest(flight, request);
        Flight updated = flightRepository.save(flight);
        if (previousStatus != updated.getStatus()) {
            publishOperationalEventIfApplicable(updated.getStatus(), updated);
        }
        return updated;
    }

    @CacheEvict(cacheNames = {"flights", "flight", "dashboard"}, allEntries = true)
    public Flight updateFlightStatus(Long id, FlightStatusUpdateRequest request) {
        Flight flight = findFlightOrThrow(id);
        FlightStatus previousStatus = flight.getStatus();
        flight.setStatus(request.status());
        if (request.delayMinutes() != null) {
            flight.setDelayMinutes(request.delayMinutes());
        }
        if (request.actualDeparture() != null) {
            flight.setActualDeparture(request.actualDeparture());
        }
        if (request.actualArrival() != null) {
            flight.setActualArrival(request.actualArrival());
        }
        Flight updated = flightRepository.save(flight);
        if (previousStatus != request.status()) {
            publishOperationalEventIfApplicable(request.status(), updated);
        }
        return updated;
    }

    private Flight findFlightOrThrow(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight %d not found".formatted(id)));
    }

    private void applyRequest(Flight flight, FlightRequest request) {
        flight.setFlightNumber(request.flightNumber());
        flight.setOrigin(request.origin());
        flight.setDestination(request.destination());
        flight.setScheduledDeparture(request.scheduledDeparture());
        flight.setScheduledArrival(request.scheduledArrival());
        flight.setActualDeparture(request.actualDeparture());
        flight.setActualArrival(request.actualArrival());
        flight.setStatus(request.status());
        flight.setDelayMinutes(request.delayMinutes() == null ? 0 : request.delayMinutes());
    }

    private void publishOperationalEventIfApplicable(FlightStatus status, Flight flight) {
        FlightEventType eventType = switch (status) {
            case DELAYED -> FlightEventType.FLIGHT_DELAYED;
            case CANCELLED -> FlightEventType.FLIGHT_CANCELLED;
            case AIRBORNE -> FlightEventType.FLIGHT_DEPARTED;
            case LANDED -> FlightEventType.FLIGHT_ARRIVED;
            default -> null;
        };

        if (eventType == null) {
            return;
        }

        flightEventPublisher.publish(new FlightEvent(
                eventType,
                flight.getId(),
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getStatus(),
                flight.getDelayMinutes(),
                LocalDateTime.now()));
    }
}
