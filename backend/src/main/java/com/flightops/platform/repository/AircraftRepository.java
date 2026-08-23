package com.flightops.platform.repository;

import com.flightops.platform.domain.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftRepository extends JpaRepository<Aircraft, Long> {

    long countByAvailableTrue();
}
