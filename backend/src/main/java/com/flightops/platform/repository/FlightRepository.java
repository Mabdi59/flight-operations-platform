package com.flightops.platform.repository;

import com.flightops.platform.domain.Flight;
import com.flightops.platform.domain.FlightStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    long countByStatus(FlightStatus status);

    List<Flight> findAllByOrderByScheduledDepartureAsc();

    long countByStatusNotIn(List<FlightStatus> statuses);
}
