package com.green.energy.api.service;

import com.green.energy.api.dto.EnergyChartPointResponse;
import com.green.energy.api.dto.EnergyMeasurementResponse;
import com.green.energy.api.entity.EnergyMeasurementEntity;
import com.green.energy.api.entity.GeneratorEntity;
import com.green.energy.api.repository.EnergyMeasurementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EnergyMeasurementService {

    private final EnergyMeasurementRepository repository;
    private final GeneratorStatusService generatorStatusService;

    public EnergyMeasurementService(
            EnergyMeasurementRepository repository,
            GeneratorStatusService generatorStatusService
    ) {
        this.repository = repository;
        this.generatorStatusService =generatorStatusService;
    }

    // =======================
    // Mapping
    // =======================
    private EnergyMeasurementResponse mapToResponse(
            EnergyMeasurementEntity e
    ) {
        GeneratorEntity g = e.getGenerator();

        EnergyMeasurementResponse r = new EnergyMeasurementResponse();
        r.timestamp = e.getTimestamp();
        r.generatorId = g.getId();
        r.generatorType = g.getType();
        r.latitude = g.getLatitude();
        r.longitude = g.getLongitude();

        r.actualPowerKw = e.getActualPowerKw();
        r.expectedPowerKw = e.getExpectedPowerKw();

        return r;
    }

    @Transactional(readOnly = true)
    public List<EnergyChartPointResponse> getChartForGenerator(
            UUID id
    ) {
        var measurements = repository.findLatestByGenerator(id);

        return measurements.stream()
                .map(e -> {
                    GeneratorStatusService.AnomalyResult anomalyRes=generatorStatusService.detect(e.getActualPowerKw(),e.getExpectedPowerKw().doubleValue());
                    return new EnergyChartPointResponse(
                            e.getTimestamp(),
                            e.getExpectedPowerKw(),
                            e.getActualPowerKw(),
                            anomalyRes.anomalous()
                    );
                })
                .toList();
    }
}
