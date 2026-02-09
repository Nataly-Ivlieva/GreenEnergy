package com.green.energy.api.service;

import com.green.energy.api.domain.AnomalyType;
import com.green.energy.api.entity.EnergyMeasurementEntity;
import com.green.energy.api.entity.GeneratorEntity;
import com.green.energy.api.ml.MlClient;
import com.green.energy.api.ml.MlPredictionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PredictionService {

    private final MlClient mlClient;

    public PredictionService(MlClient mlClient) {
        this.mlClient = mlClient;
    }

    public double predict(GeneratorEntity g, EnergyMeasurementEntity weather) {
        if(weather==null) return 0.0;
        MlPredictionRequest req = new MlPredictionRequest();
        req.timestamp = weather.getTimestamp().toEpochSecond();
        req.generatorType = g.getType().name();
        req.maxCapacityKw = g.getMaxCapacityKw();

        req.temperatureC = weather.getWeather().getTemperatureC();
        req.windSpeedMs = weather.getWeather().getWindSpeedMs();
        req.solarIrradianceWm2 = weather.getWeather().getSolarIrradianceWm2();
        req.precipitationMm = weather.getWeather().getPrecipitationMm();
        req.cloudCover = weather.getWeather().getCloudCover();

        try {
            return mlClient.predictExpectedPower(req);
        } catch (Exception e) {
            log.warn("ML prediction failed for generator {}", g.getId(), e);
            return 0.0;
        }
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

