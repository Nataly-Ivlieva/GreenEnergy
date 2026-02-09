package com.green.energy.simulator.power;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PowerOutput {

    private double expectedKw;
    private double actualKw;

    private boolean anomalous;
    private String anomalyType;
}
