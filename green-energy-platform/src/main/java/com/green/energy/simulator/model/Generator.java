package com.green.energy.simulator.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Generator {

    private String id;
    private GeneratorType type;

    private double latitude;
    private double longitude;

    /** Nominal (max) power in kW */
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
