package com.green.energy.simulation.power;

import com.green.energy.simulation.weather.WeatherSnapshot;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class SolarPowerModel {

    public double expected(double nominalKw, WeatherSnapshot w) {

        double cf =
                irradianceCf(w.solarIrradianceWm2())
                        * cloudCf(w.cloudCover())
                        * temperatureCf(w.temperatureC())
                        * seasonalCf(w.timestamp());

        cf = clamp(cf, 0.0, 1.0);

        return nominalKw * cf;
    }

    // ------------------------------

    private double irradianceCf(double wm2) {
        if (wm2 <= 0) return 0.0;
        return clamp(wm2 / 800.0, 0.0, 1.0);
    }

    private double cloudCf(double cloudCover) {
        return 1.0 - 0.35 * clamp(cloudCover, 0.0, 1.0);
    }

    private double temperatureCf(double tempC) {
        if (tempC < -10) return 0.95;
        if (tempC < 0)   return 0.98;
        if (tempC <= 25) return 1.0;

        return Math.max(0.9, 1.0 - (tempC - 25) * 0.005);
    }

    private double seasonalCf(Instant ts) {
        int doy = ZonedDateTime.ofInstant(ts, ZoneOffset.UTC).getDayOfYear();
        return 0.95 + 0.05 * Math.cos(2 * Math.PI * (doy - 172) / 365);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
