package com.green.energy.simulation.power;

import com.green.energy.simulation.weather.WeatherSnapshot;

public class WindPowerModel {

    private static final double CUT_IN  = 3.0;
    private static final double RATED   = 12.0;
    private static final double CUT_OUT = 25.0;

    public double expected(double nominalKw, WeatherSnapshot w) {

        double windCf = windCf(w.windSpeedMs());
        double airCf  = airDensityCf(w.temperatureC());

        // air density matters mostly below rated
        double cf = windCf < 1.0
                ? windCf * airCf
                : windCf;

        cf = clamp(cf, 0.0, 1.0);

        return nominalKw * cf;
    }

    // ------------------------------
    // Capacity factor from wind speed
    // ------------------------------
    private double windCf(double v) {

        if (v < CUT_IN || v >= CUT_OUT) return 0.0;

        if (v < RATED) {
            double x = (v - CUT_IN) / (RATED - CUT_IN);
            return Math.pow(x, 3); // плавный рост
        }

        return 1.0; // rated plateau
    }

    // ------------------------------
    // Cold air → slightly more power
    // ------------------------------
    private double airDensityCf(double tempC) {

        // ≈ +3% при −10°C, −2% при +30°C
        double cf = 1.0 + (10.0 - tempC) * 0.0015;

        return clamp(cf, 0.97, 1.03);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
