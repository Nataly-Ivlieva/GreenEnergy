package com.green.energy.ingestion.service;

import com.green.energy.ingestion.entity.EnergyMeasurementEntity;
import com.green.energy.ingestion.ml.MlClient;
import com.green.energy.ingestion.ml.MlPredictionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class PredictionService {

    private final MlClient mlClient;

    public PredictionService(MlClient mlClient) {
        this.mlClient = mlClient;
    }

    public List<Double> predictBatch(List<EnergyMeasurementEntity> measurements) {

        if (measurements == null || measurements.isEmpty()) {
            return Collections.emptyList();
        }

        List<MlPredictionRequest> batch = measurements.stream()
                .map(this::toMlRequest)
                .toList();

        try {
            return mlClient.predictExpectedPowerBatch(batch);
        } catch (Exception e) {
            log.warn("ML batch prediction failed", e);
            return Collections.nCopies(batch.size(), 0.0);
        }
    }

    private MlPredictionRequest toMlRequest(EnergyMeasurementEntity m) {
        return new MlPredictionRequest(
                m.getTimestamp().toEpochSecond(),
                m.getGenerator().getType().name(),
                m.getGenerator().getMaxCapacityKw(),
                m.getWeather().getTemperatureC(),
                m.getWeather().getWindSpeedMs(),
                m.getWeather().getSolarIrradianceWm2(),
                m.getWeather().getPrecipitationMm(),
                m.getWeather().getCloudCover()
        );
    }
}
