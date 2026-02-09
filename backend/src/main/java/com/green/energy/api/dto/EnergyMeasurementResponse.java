package com.green.energy.api.dto;

import com.green.energy.api.domain.AnomalyType;
import com.green.energy.api.domain.GeneratorType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class EnergyMeasurementResponse {

    public OffsetDateTime timestamp;

    public UUID generatorId;
    public GeneratorType generatorType;
    public double latitude;
    public double longitude;

    public double expectedPowerKw;
    public Double actualPowerKw;

    public boolean anomalous;
    public AnomalyType anomalyType;

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getGeneratorId() {
        return generatorId;
    }

    public void setGeneratorId(UUID generatorId) {
        this.generatorId = generatorId;
    }

    public GeneratorType getGeneratorType() {
        return generatorType;
    }

    public void setGeneratorType(GeneratorType generatorType) {
        this.generatorType = generatorType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getExpectedPowerKw() {
        return expectedPowerKw;
    }

    public void setExpectedPowerKw(double expectedPowerKw) {
        this.expectedPowerKw = expectedPowerKw;
    }

    public Double getActualPowerKw() {
        return actualPowerKw;
    }

    public void setActualPowerKw(Double actualPowerKw) {
        this.actualPowerKw = actualPowerKw;
    }

    public boolean isAnomalous() {
        return anomalous;
    }

    public void setAnomalous(boolean anomalous) {
        this.anomalous = anomalous;
    }

    public AnomalyType getAnomalyType() {
        return anomalyType;
    }

    public void setAnomalyType(AnomalyType anomalyType) {
        this.anomalyType = anomalyType;
    }
}
