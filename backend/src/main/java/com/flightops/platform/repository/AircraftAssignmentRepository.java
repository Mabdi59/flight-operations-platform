package com.flightops.platform.repository;

import com.flightops.platform.domain.AircraftAssignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftAssignmentRepository extends JpaRepository<AircraftAssignment, Long> {

    List<AircraftAssignment> findTop10ByOrderByAssignedAtDesc();
}
