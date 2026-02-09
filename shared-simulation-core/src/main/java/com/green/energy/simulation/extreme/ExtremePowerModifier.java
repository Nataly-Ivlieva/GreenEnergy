package com.green.energy.simulation.extreme;

import com.green.energy.simulation.power.PowerOutput;
import com.green.energy.simulation.generator.GeneratorType;

public class ExtremePowerModifier {

    public PowerOutput apply(
            PowerOutput normal,
            GeneratorType type,
            ExtremeCondition condition,
            double maxCapacity
    ) {

        if (condition == ExtremeCondition.NONE) {
            return normal;
        }

        double factor = switch (condition) {

            case HEAT_WAVE -> switch (type) {
                case SOLAR -> 0.9;
                case WIND -> 1.0;
                case HYDRO -> 0.95;
            };

            case EXTREME_COLD -> switch (type) {
                case SOLAR -> 0.8;
                case WIND -> 0.85;
                case HYDRO -> 0.9;
            };

            case STORM -> switch (type) {
                case SOLAR -> 0.2;
                case WIND -> 0.0;
                case HYDRO -> 1.2;
            };

            case HURRICANE -> switch (type) {
                case SOLAR -> 0.1;
                case WIND -> 0.0;
                case HYDRO -> 1.4;
            };

            case LONG_HEAVY_RAIN -> switch (type) {
                case SOLAR -> 0.6;
                case WIND -> 1.0;
                case HYDRO -> 1.2;
            };

            case FLOOD_RISK -> switch (type) {
                case SOLAR -> 0.5;
                case WIND -> 0.9;
                case HYDRO -> 1.5;
            };

            case DUST_STORM -> switch (type) {
                case SOLAR -> 0.3;
                default -> 1.0;
            };

            case ICING_RISK -> switch (type) {
                case WIND -> 0.5;
                default -> 1.0;
            };

            default -> 1.0;
        };

        double newActual = Math.min(
                normal.getActualKw() * factor,
                maxCapacity * 1.6
        );

        return PowerOutput.builder()
                .expectedKw(normal.getExpectedKw())
                .actualKw(newActual)
                .anomalous(normal.isAnomalous())
                .anomalyType(normal.getAnomalyType())
                .build();
    }
}

