package com.green.energy.api.controller;

import com.green.energy.api.domain.GeneratorType;
import com.green.energy.api.dto.EnergyChartPointResponse;
import com.green.energy.api.service.EnergyMeasurementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/charts")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class EnergyMeasurementController {

    private final EnergyMeasurementService service;

    public EnergyMeasurementController(EnergyMeasurementService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<EnergyChartPointResponse> generator(
             @RequestParam(required = false) UUID id
    ) {
        return service.getChartForGenerator(id);
    }

}
