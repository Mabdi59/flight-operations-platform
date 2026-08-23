package com.flightops.platform.service;

import com.flightops.platform.domain.OperationalAlert;
import com.flightops.platform.repository.OperationalAlertRepository;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OperationalAlertService {

    private final OperationalAlertRepository operationalAlertRepository;

    public OperationalAlertService(OperationalAlertRepository operationalAlertRepository) {
        this.operationalAlertRepository = operationalAlertRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable("alerts")
    public List<OperationalAlert> getRecentAlerts() {
        return operationalAlertRepository.findTop20ByOrderByCreatedAtDesc();
    }

    @CacheEvict(cacheNames = {"alerts", "dashboard"}, allEntries = true)
    public OperationalAlert createAlert(OperationalAlert alert) {
        return operationalAlertRepository.save(alert);
    }
}
