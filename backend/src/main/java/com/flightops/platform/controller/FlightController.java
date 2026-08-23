package com.flightops.platform.controller;

import com.flightops.platform.domain.Flight;
import com.flightops.platform.dto.FlightRequest;
import com.flightops.platform.dto.FlightStatusUpdateRequest;
import com.flightops.platform.service.FlightService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public List<Flight> getFlights() {
        return flightService.getFlights();
    }

    @GetMapping("/{id}")
    public Flight getFlight(@PathVariable Long id) {
        return flightService.getFlight(id);
    }

    @PostMapping
    public ResponseEntity<Flight> createFlight(@Valid @RequestBody FlightRequest request) {
        Flight flight = flightService.createFlight(request);
        return ResponseEntity.created(URI.create("/api/flights/" + flight.getId())).body(flight);
    }

    @PutMapping("/{id}")
    public Flight updateFlight(@PathVariable Long id, @Valid @RequestBody FlightRequest request) {
        return flightService.updateFlight(id, request);
    }

    @PatchMapping("/{id}/status")
    public Flight updateFlightStatus(@PathVariable Long id, @Valid @RequestBody FlightStatusUpdateRequest request) {
        return flightService.updateFlightStatus(id, request);
    }
}
