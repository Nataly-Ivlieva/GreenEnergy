package com.green.energy.api.service;

import com.green.energy.api.domain.GeneratorType;
import com.green.energy.api.dto.EnergyChartPointResponse;
import com.green.energy.api.dto.EnergyMeasurementResponse;
import com.green.energy.api.entity.EnergyMeasurementEntity;
import com.green.energy.api.entity.GeneratorEntity;
import com.green.energy.api.repository.EnergyMeasurementRepository;
import com.green.energy.api.repository.GeneratorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class EnergyMeasurementService {

    private final EnergyMeasurementRepository repository;
    private final GeneratorRepository generatorRepository;
    private final PredictionService predictionService;

    public EnergyMeasurementService(
            EnergyMeasurementRepository repository,
            GeneratorRepository generatorRepository,
            PredictionService predictionService
    ) {
        this.repository = repository;
        this.generatorRepository = generatorRepository;
        this.predictionService = predictionService;
    }

    // =======================
    // Mapping
    // =======================
    private EnergyMeasurementResponse mapToResponse(
            EnergyMeasurementEntity e
    ) {
        GeneratorEntity g = e.getGenerator();

        double expected = predictionService.predict(g, e);
        PredictionService.AnomalyResult anomaly =
                predictionService.detect(e.getActualPowerKw(), expected);

        EnergyMeasurementResponse r = new EnergyMeasurementResponse();
        r.timestamp = e.getTimestamp();
        r.generatorId = g.getId();
        r.generatorType = g.getType();
        r.latitude = g.getLatitude();
        r.longitude = g.getLongitude();

        r.actualPowerKw = e.getActualPowerKw();
        r.expectedPowerKw = expected;
        r.anomalous = anomaly.anomalous();
        r.anomalyType = anomaly.type();

        return r;
    }

    // =======================
    // Queries
    // =======================
    @Transactional(readOnly = true)
    public List<EnergyMeasurementResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EnergyChartPointResponse> getChart(
            OffsetDateTime from,
            OffsetDateTime to,
            GeneratorType type
    ) {

        if (to == null) {
            to = repository.findMaxTimestamp();
            if (to == null) {
                return List.of();
            }
        }

        if (from == null) {
            from = to.minusDays(7); // дефолтный период
        }

        var measurements = repository.findForChart(from, to, type);

        return measurements.stream()
                .map(e -> {
                    GeneratorEntity g = e.getGenerator();

                    double expected = predictionService.predict(g, e);

                    var anomaly = predictionService.detect(
                            e.getActualPowerKw(),
                            expected
                    );

                    return new EnergyChartPointResponse(
                            e.getTimestamp(),
                            expected,
                            e.getActualPowerKw(),
                            anomaly.anomalous()
                    );
                })
                .toList();
    }
}
