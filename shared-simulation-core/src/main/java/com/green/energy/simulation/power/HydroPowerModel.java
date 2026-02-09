package com.green.energy.simulation.power;

import com.green.energy.simulation.weather.WeatherSnapshot;

public class HydroPowerModel {

    private static final double BASE_CF = 0.58;
    private static final double MAX_CF  = 1.02;

    public double expected(double nominalKw, WeatherSnapshot w) {

        double cf = BASE_CF;

        cf += precipitationCf(w.precipitationMm());

        cf *= freezingLoss(w.temperatureC());

        cf *= floodLoss(w.precipitationMm());

        cf = clamp(cf, 0.0, MAX_CF);

        return nominalKw * cf;
    }

    // ------------------------------

    private double precipitationCf(double mm) {
        if (mm <= 0) return 0.0;

        double boost = mm / 60.0;
        return clamp(boost, 0.0, 0.25);
    }

    private double freezingLoss(double tempC) {

        if (tempC >= 0) return 1.0;
        if (tempC <= -8) return 0.9;

        return 1.0 - (-tempC * 0.0125);
    }

    private double floodLoss(double mm) {
        if (mm < 80) return 1.0;

        double loss = (mm - 80) * 0.002;
        return clamp(1.0 - loss, 0.9, 1.0);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
