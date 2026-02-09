package com.green.energy.api.service;

import com.green.energy.api.dto.GeneratorStatusResponse;
import com.green.energy.api.entity.EnergyMeasurementEntity;
import com.green.energy.api.entity.GeneratorEntity;
import com.green.energy.api.ml.MlClient;
import com.green.energy.api.repository.EnergyMeasurementRepository;
import com.green.energy.api.repository.GeneratorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneratorStatusService {

    private final GeneratorRepository generatorRepo;
    private final EnergyMeasurementRepository measurementRepo;
    private final MlClient mlClient;
    private final PredictionService predictionService;


    public GeneratorStatusService(
            GeneratorRepository generatorRepo,
            EnergyMeasurementRepository measurementRepo,
            MlClient mlClient,
            PredictionService predictionService
    ) {
        this.generatorRepo = generatorRepo;
        this.measurementRepo = measurementRepo;
        this.mlClient = mlClient;
        this.predictionService = predictionService;
    }

    public List<GeneratorStatusResponse> getCurrentStatus() {

        return generatorRepo.findAll().stream()
                .map(this::buildStatus)
                .toList();
    }

    private GeneratorStatusResponse buildStatus(GeneratorEntity g) {

        EnergyMeasurementEntity last =
                measurementRepo.findTopByGeneratorOrderByTimestampDesc(g);
        System.out.println(g.getId()+g.getName());

        double expected = last != null
                ? predictionService.predict(g, last)
                : 0.0;

        PredictionService.AnomalyResult anomaly =
                predictionService.detect(
                        last != null ? last.getActualPowerKw() : null,
                        expected
                );
        System.out.println(g.getId()+g.getName());
        return new GeneratorStatusResponse(
                g.getId(),
                g.getName(),
                g.getType(),
                g.getLatitude(),
                g.getLongitude(),
                expected,
                last != null ? last.getActualPowerKw() : null,
                anomaly.anomalous(),
                anomaly.type(),
                last != null ? last.getTimestamp() : null
        );
    }
}
