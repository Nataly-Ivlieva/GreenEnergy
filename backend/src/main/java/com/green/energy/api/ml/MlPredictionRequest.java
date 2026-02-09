package com.green.energy.api.ml;

public class MlPredictionRequest {

    public Long timestamp;
    public String generatorType;
    public Double maxCapacityKw;
    public Double temperatureC;
    public Double windSpeedMs;
    public Double solarIrradianceWm2;
    public Double precipitationMm;
    public Double cloudCover;
}

