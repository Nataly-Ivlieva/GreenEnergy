package com.green.energy.simulation.anomaly;

import com.green.energy.simulation.power.PowerOutput;
import com.green.energy.simulation.generator.GeneratorType;

import java.util.Random;

public class AnomalyInjector {

    private static final double BASE_PROBABILITY = 0.02;
    private final Random random = new Random();

    public PowerOutput injectIfNeeded(
            PowerOutput normal,
            GeneratorType type,
            double maxCapacity
    ) {

        if (random.nextDouble() > BASE_PROBABILITY) {
            return normal;
        }

        double expected = normal.getExpectedKw();
        double actual;

        switch (type) {

            case WIND -> {
                double r = random.nextDouble();

                if (r < 0.6) { // partial curtailment
                    actual = expected * randomBetween(0.4, 0.7);
                } else if (r < 0.9) { // minor fault
                    actual = expected * randomBetween(0.7, 0.9);
                } else { // rare full stop
                    actual = expected * randomBetween(0.05, 0.2);
                }
            }

            case SOLAR -> {
                if (expected < maxCapacity * 0.05) {
                    actual = 0.0; // night / near-night
                } else {
                    double r = random.nextDouble();
                    if (r < 0.7) {
                        actual = expected * randomBetween(0.5, 0.8);
                    } else {
                        actual = expected * randomBetween(0.9, 1.05);
                    }
                }
            }

            case HYDRO -> {
                double r = random.nextDouble();
                if (r < 0.6) {
                    actual = expected * randomBetween(0.6, 0.85);
                } else if (r < 0.9) {
                    actual = expected * randomBetween(0.85, 1.05);
                } else {
                    actual = Math.min(
                            expected * randomBetween(1.1, 1.2),
                            maxCapacity * 1.2
                    );
                }
            }

            default -> actual = normal.getActualKw();
        }

        actual = clamp(actual, 0, maxCapacity);

        return PowerOutput.builder()
                .expectedKw(expected)
                .actualKw(actual)
                .anomalous(true)
                .anomalyType(type.name() + "_FAULT")
                .build();
    }

    private double randomBetween(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
