package com.green.energy.simulation.noise;

import com.green.energy.simulation.power.PowerOutput;

public class NoiseApplier {

    private final NoiseGenerator generator = new NoiseGenerator();

    public PowerOutput apply(PowerOutput base) {

        double noisyActual =
                generator.apply(base.getActualKw());

        return PowerOutput.builder()
                .expectedKw(base.getExpectedKw())
                .actualKw(noisyActual)
                .anomalous(base.isAnomalous())
                .anomalyType(base.getAnomalyType())
                .build();
    }
}
