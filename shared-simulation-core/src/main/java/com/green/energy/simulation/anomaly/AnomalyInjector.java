package com.green.energy.simulator.anomaly;

import com.green.energy.simulator.model.GeneratorType;
import com.green.energy.simulator.power.PowerOutput;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
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

        return switch (type) {
            case SOLAR -> solarAnomaly(normal, maxCapacity);
            case WIND -> windAnomaly(normal, maxCapacity);
            case HYDRO -> hydroAnomaly(normal, maxCapacity);
        };
    }

    // ================= SOLAR =================

    private PowerOutput solarAnomaly(PowerOutput normal, double maxCapacity) {

        int pick = random.nextInt(4);

        double actual = switch (pick) {

            // inverter failure
            case 0 -> 0.0;

            // shading / dirt
            case 1 -> normal.getExpectedKw() * randomBetween(0.5, 0.8);

            // sensor spike
            case 2 -> Math.min(
                    normal.getExpectedKw() * randomBetween(1.1, 1.4),
                    maxCapacity * 1.1
            );

            // string failure
            default -> normal.getExpectedKw() * randomBetween(0.6, 0.9);
        };

        return anomalous(normal, actual, "SOLAR_FAULT");
    }

    // ================= WIND =================

    private PowerOutput windAnomaly(PowerOutput normal, double maxCapacity) {

        int pick = random.nextInt(4);

        double actual = switch (pick) {

            // yaw error
            case 0 -> normal.getExpectedKw() * randomBetween(0.7, 0.9);

            // braking
            case 1 -> 0.0;

            // storm shutdown mimic
            case 2 -> 0.0;

            // spike but limited
            default -> Math.min(
                    normal.getExpectedKw() * randomBetween(1.2, 1.6),
                    maxCapacity * 1.2
            );
        };

        return anomalous(normal, actual, "WIND_FAULT");
    }

    // ================= HYDRO =================

    private PowerOutput hydroAnomaly(PowerOutput normal, double maxCapacity) {

        int pick = random.nextInt(4);

        double actual = switch (pick) {

            // turbine stop
            case 0 -> 0.0;

            // flood spike
            case 1 -> Math.min(
                    normal.getExpectedKw() * randomBetween(1.3, 2.0),
                    maxCapacity * 1.5
            );

            // cavitation oscillation
            case 2 -> normal.getExpectedKw() * randomBetween(0.8, 1.2);

            // stuck gate flatline
            default -> normal.getActualKw();
        };

        return anomalous(normal, actual, "HYDRO_FAULT");
    }

    // ================= Helpers =================

    private PowerOutput anomalous(PowerOutput normal, double actual, String type) {

        return PowerOutput.builder()
                .expectedKw(normal.getExpectedKw())
                .actualKw(actual)
                .anomalous(true)
                .anomalyType(type)
                .build();
    }

    private double randomBetween(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
