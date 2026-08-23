package com.flightops.platform.service;

import com.flightops.platform.domain.FlightStatus;
import com.flightops.platform.dto.AircraftAssignmentSummary;
import com.flightops.platform.dto.DashboardResponse;
import com.flightops.platform.dto.FlightSummary;
import com.flightops.platform.dto.OperationalAlertSummary;
import com.flightops.platform.repository.AircraftAssignmentRepository;
import com.flightops.platform.repository.AircraftRepository;
import com.flightops.platform.repository.FlightRepository;
import com.flightops.platform.repository.OperationalAlertRepository;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;
    private final AircraftAssignmentRepository aircraftAssignmentRepository;
    private final OperationalAlertRepository operationalAlertRepository;

    public DashboardService(FlightRepository flightRepository, AircraftRepository aircraftRepository,
            AircraftAssignmentRepository aircraftAssignmentRepository, OperationalAlertRepository operationalAlertRepository) {
        this.flightRepository = flightRepository;
        this.aircraftRepository = aircraftRepository;
        this.aircraftAssignmentRepository = aircraftAssignmentRepository;
        this.operationalAlertRepository = operationalAlertRepository;
    }

    @Cacheable("dashboard")
    public DashboardResponse getDashboard() {
        List<FlightSummary> flights = flightRepository.findAllByOrderByScheduledDepartureAsc().stream()
                .map(flight -> new FlightSummary(
                        flight.getId(),
                        flight.getFlightNumber(),
                        flight.getOrigin(),
                        flight.getDestination(),
                        flight.getScheduledDeparture(),
                        flight.getScheduledArrival(),
                        flight.getStatus(),
                        flight.getDelayMinutes()))
                .toList();

        List<AircraftAssignmentSummary> assignments = aircraftAssignmentRepository.findTop10ByOrderByAssignedAtDesc().stream()
                .map(assignment -> new AircraftAssignmentSummary(
                        assignment.getId(),
                        assignment.getFlight().getFlightNumber(),
                        assignment.getAircraft().getTailNumber(),
                        assignment.getAircraft().getModel(),
                        assignment.getAssignedAt()))
                .toList();

        List<OperationalAlertSummary> alerts = operationalAlertRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(alert -> new OperationalAlertSummary(
                        alert.getId(),
                        alert.getEventType(),
                        alert.getSeverity(),
                        alert.getMessage(),
                        alert.getCreatedAt()))
                .toList();

        return new DashboardResponse(
                flightRepository.countByStatusNotIn(List.of(FlightStatus.LANDED, FlightStatus.CANCELLED)),
                flightRepository.countByStatus(FlightStatus.DELAYED),
                flightRepository.countByStatus(FlightStatus.CANCELLED),
                aircraftRepository.countByAvailableTrue(),
                flights,
                assignments,
                alerts);
    }
}
