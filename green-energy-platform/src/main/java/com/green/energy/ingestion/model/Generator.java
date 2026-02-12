package com.green.energy.ingestion.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Generator {
    private String id;
    private GeneratorType type;
    private double latitude;
    private double longitude;
    private double nominalPowerKw;
    public GeneratorType getType() {
        return type;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public double getNominalPowerKw() {
        return nominalPowerKw;
    }
}
