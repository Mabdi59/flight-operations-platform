package com.flightops.platform.repository;

import com.flightops.platform.domain.CrewAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewAssignmentRepository extends JpaRepository<CrewAssignment, Long> {
}
