package com.green.energy.simulation.power;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PowerOutput {

    double expectedKw;
    double actualKw;

    boolean anomalous;
    String anomalyType;
}

