package com.flightops.platform.controller;

import com.flightops.platform.domain.OperationalAlert;
import com.flightops.platform.service.OperationalAlertService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
public class OperationalAlertController {

    private final OperationalAlertService operationalAlertService;

    public OperationalAlertController(OperationalAlertService operationalAlertService) {
        this.operationalAlertService = operationalAlertService;
    }

    @GetMapping
    public List<OperationalAlert> getAlerts() {
        return operationalAlertService.getRecentAlerts();
    }
}
