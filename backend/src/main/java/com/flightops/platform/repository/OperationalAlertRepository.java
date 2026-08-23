package com.flightops.platform.repository;

import com.flightops.platform.domain.OperationalAlert;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationalAlertRepository extends JpaRepository<OperationalAlert, Long> {

    List<OperationalAlert> findTop10ByOrderByCreatedAtDesc();

    List<OperationalAlert> findTop20ByOrderByCreatedAtDesc();
}
