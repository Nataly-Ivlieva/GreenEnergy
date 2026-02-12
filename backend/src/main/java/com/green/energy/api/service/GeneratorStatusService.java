package com.green.energy.api.service;

import com.green.energy.api.domain.AnomalyType;
import com.green.energy.api.dto.GeneratorStatusResponse;
import com.green.energy.api.entity.EnergyMeasurementEntity;
import com.green.energy.api.entity.GeneratorEntity;
import com.green.energy.api.repository.EnergyMeasurementRepository;
import com.green.energy.api.repository.GeneratorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneratorStatusService {

    private final GeneratorRepository generatorRepo;
    private final EnergyMeasurementRepository measurementRepo;

    public GeneratorStatusService(
            GeneratorRepository generatorRepo,
            EnergyMeasurementRepository measurementRepo
    ) {
        this.generatorRepo = generatorRepo;
        this.measurementRepo = measurementRepo;
    }

    public List<GeneratorStatusResponse> getCurrentStatus() {

        return generatorRepo.findAll().stream()
                .map(this::buildStatus)
                .toList();
    }

    private GeneratorStatusResponse buildStatus(GeneratorEntity g) {

        EnergyMeasurementEntity last =
                measurementRepo.findTopByGeneratorOrderByTimestampDesc(g);

        AnomalyResult anomaly =
                detect(
                        last != null ? last.getActualPowerKw() : null,
                        last.getExpectedPowerKw()
                );
        return new GeneratorStatusResponse(
                g.getId(),
                g.getName(),
                g.getType(),
                g.getLatitude(),
                g.getLongitude(),
                last.getExpectedPowerKw(),
                last != null ? last.getActualPowerKw() : null,
                anomaly.anomalous(),
                anomaly.type(),
                last != null ? last.getTimestamp() : null
        );
    }
    public AnomalyResult detect(Double actual, double expected) {

        if (actual == null || expected <= 0) {
            return new AnomalyResult(false, null);
        }

        double error = Math.abs(actual - expected) / expected;

        if (error > 0.3) {
            return new AnomalyResult(true, AnomalyType.CRITICAL);
        }
        if (error > 0.15) {
            return new AnomalyResult(true, AnomalyType.WARNING);
        }

        return new AnomalyResult(false, null);
    }

    public record AnomalyResult(
            boolean anomalous,
            AnomalyType type
    ) {}
}