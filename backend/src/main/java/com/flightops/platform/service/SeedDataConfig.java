package com.flightops.platform.service;

import com.flightops.platform.domain.Aircraft;
import com.flightops.platform.domain.AircraftAssignment;
import com.flightops.platform.domain.AircraftStatus;
import com.flightops.platform.domain.AlertSeverity;
import com.flightops.platform.domain.CrewAssignment;
import com.flightops.platform.domain.CrewMember;
import com.flightops.platform.domain.Flight;
import com.flightops.platform.domain.FlightEventType;
import com.flightops.platform.domain.FlightStatus;
import com.flightops.platform.domain.OperationalAlert;
import com.flightops.platform.repository.AircraftAssignmentRepository;
import com.flightops.platform.repository.AircraftRepository;
import com.flightops.platform.repository.CrewAssignmentRepository;
import com.flightops.platform.repository.CrewMemberRepository;
import com.flightops.platform.repository.FlightRepository;
import com.flightops.platform.repository.OperationalAlertRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {

    @Bean
    @ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedData(
            FlightRepository flightRepository,
            AircraftRepository aircraftRepository,
            CrewMemberRepository crewMemberRepository,
            AircraftAssignmentRepository aircraftAssignmentRepository,
            CrewAssignmentRepository crewAssignmentRepository,
            OperationalAlertRepository operationalAlertRepository) {
        return args -> {
            if (flightRepository.count() > 0) {
                return;
            }

            LocalDateTime baseTime = LocalDateTime.now().withSecond(0).withNano(0).plusHours(1);

            Aircraft a1 = createAircraft("N320AZ", "Airbus A320neo", AircraftStatus.OPERATIONAL, true);
            Aircraft a2 = createAircraft("N738AZ", "Boeing 737-800", AircraftStatus.OPERATIONAL, false);
            Aircraft a3 = createAircraft("N789AZ", "Boeing 787-9", AircraftStatus.RESERVED, true);
            aircraftRepository.saveAll(List.of(a1, a2, a3));

            CrewMember c1 = createCrew("CM-1001", "Amelia Torres", "Captain", true);
            CrewMember c2 = createCrew("CM-1002", "Jordan Ellis", "First Officer", true);
            CrewMember c3 = createCrew("CM-1003", "Priya Shah", "Cabin Manager", false);
            crewMemberRepository.saveAll(List.of(c1, c2, c3));

            Flight f1 = createFlight("AZ101", "JFK", "LAX", baseTime, baseTime.plusHours(6), FlightStatus.BOARDING, 0, null, null);
            Flight f2 = createFlight("AZ205", "ORD", "MIA", baseTime.plusMinutes(45), baseTime.plusHours(4), FlightStatus.DELAYED, 35, null, null);
            Flight f3 = createFlight("AZ330", "SEA", "DFW", baseTime.minusHours(1), baseTime.plusHours(2), FlightStatus.AIRBORNE, 0, baseTime.minusMinutes(20), null);
            Flight f4 = createFlight("AZ412", "BOS", "ATL", baseTime.plusHours(2), baseTime.plusHours(5), FlightStatus.CANCELLED, 0, null, null);
            flightRepository.saveAll(List.of(f1, f2, f3, f4));

            aircraftAssignmentRepository.saveAll(List.of(
                    createAircraftAssignment(f1, a1, baseTime.minusHours(2)),
                    createAircraftAssignment(f2, a2, baseTime.minusHours(1)),
                    createAircraftAssignment(f3, a3, baseTime.minusHours(3))));

            crewAssignmentRepository.saveAll(List.of(
                    createCrewAssignment(f1, c1, "Captain"),
                    createCrewAssignment(f1, c2, "First Officer"),
                    createCrewAssignment(f2, c3, "Cabin Manager")));

            operationalAlertRepository.saveAll(List.of(
                    createAlert(FlightEventType.FLIGHT_DELAYED, AlertSeverity.WARNING,
                            "AZ205 delayed by 35 minutes because of inbound crew constraints."),
                    createAlert(FlightEventType.FLIGHT_CANCELLED, AlertSeverity.CRITICAL,
                            "AZ412 cancelled because of a severe weather system over Atlanta.")));
        };
    }

    private Aircraft createAircraft(String tailNumber, String model, AircraftStatus status, boolean available) {
        Aircraft aircraft = new Aircraft();
        aircraft.setTailNumber(tailNumber);
        aircraft.setModel(model);
        aircraft.setStatus(status);
        aircraft.setAvailable(available);
        return aircraft;
    }

    private CrewMember createCrew(String employeeNumber, String fullName, String role, boolean available) {
        CrewMember crewMember = new CrewMember();
        crewMember.setEmployeeNumber(employeeNumber);
        crewMember.setFullName(fullName);
        crewMember.setRole(role);
        crewMember.setAvailable(available);
        return crewMember;
    }

    private Flight createFlight(String flightNumber, String origin, String destination, LocalDateTime scheduledDeparture,
            LocalDateTime scheduledArrival, FlightStatus status, Integer delayMinutes, LocalDateTime actualDeparture,
            LocalDateTime actualArrival) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setOrigin(origin);
        flight.setDestination(destination);
        flight.setScheduledDeparture(scheduledDeparture);
        flight.setScheduledArrival(scheduledArrival);
        flight.setStatus(status);
        flight.setDelayMinutes(delayMinutes);
        flight.setActualDeparture(actualDeparture);
        flight.setActualArrival(actualArrival);
        return flight;
    }

    private AircraftAssignment createAircraftAssignment(Flight flight, Aircraft aircraft, LocalDateTime assignedAt) {
        AircraftAssignment assignment = new AircraftAssignment();
        assignment.setFlight(flight);
        assignment.setAircraft(aircraft);
        assignment.setAssignedAt(assignedAt);
        return assignment;
    }

    private CrewAssignment createCrewAssignment(Flight flight, CrewMember crewMember, String dutyRole) {
        CrewAssignment assignment = new CrewAssignment();
        assignment.setFlight(flight);
        assignment.setCrewMember(crewMember);
        assignment.setDutyRole(dutyRole);
        return assignment;
    }

    private OperationalAlert createAlert(FlightEventType eventType, AlertSeverity severity, String message) {
        OperationalAlert alert = new OperationalAlert();
        alert.setEventType(eventType);
        alert.setSeverity(severity);
        alert.setMessage(message);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setAcknowledged(false);
        return alert;
    }
}
