package com.green.energy.api.controller.dto;

import com.green.energy.api.domain.AnomalyType;
import com.green.energy.api.domain.GeneratorType;

import java.time.Instant;

public class EnergyMeasurementRequest {

    public String generatorId;
    public String generatorType;
    public Double latitude;
    public Double longitude;
    public Instant timestamp;

    public Double maxCapacityKw;
    public Double temperatureC;
    public Double windSpeedMs;
    public Double solarIrradianceWm2;
    public Double precipitationMm;
    public Double cloudCover;

    public Double actualPowerKw;
}

