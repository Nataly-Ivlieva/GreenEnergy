package com.green.energy.ingestion.ml;

public record MlPredictionRequest(
        long timestamp,
        String generatorType,
        double maxCapacityKw,
        double temperatureC,
        double windSpeedMs,
        double solarIrradianceWm2,
        double precipitationMm,
        double cloudCover
) {}

