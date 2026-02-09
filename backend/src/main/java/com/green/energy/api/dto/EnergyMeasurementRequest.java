package com.green.energy.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class EnergyMeasurementRequest {

    public UUID generatorId;
    public String generatorType;

    public Double latitude;
    public Double longitude;
    public OffsetDateTime timestamp;

    public Double expectedPowerKw;
    public Double actualPowerKw;

    public Boolean anomalous;
    public String anomalyType;
    public Double maxCapacityKw;
    public Double temperatureC;
    public Double windSpeedMs;
    public Double solarIrradianceWm2;
    public Double precipitationMm;
    public Double cloudCover;
}