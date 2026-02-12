package com.green.energy.ingestion.season;

import java.time.Instant;
import java.time.ZoneOffset;

public class SeasonFactorCalculator {

    public double seasonalFactor(Instant time) {

        int month = time.atOffset(ZoneOffset.UTC).getMonthValue();

        return switch (month) {
            case 12, 1, 2 -> 0.85;
            case 6, 7, 8 -> 1.05;
            default -> 1.0;
        };
    }
}
