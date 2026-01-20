package com.green.energy.simulator.weather;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class WeatherMapper {

    public static WeatherTimeSeries map(OpenMeteoResponse response) {

        List<WeatherDataPoint> points = new ArrayList<>();

        var h = response.getHourly();

        for (int i = 0; i < h.getTime().size(); i++) {
            String raw = h.getTime().get(i);

            Instant timestamp =
                    raw.length() == 16
                            ? Instant.parse(raw + ":00Z")
                            : Instant.parse(raw + "Z");
            points.add(new WeatherDataPoint(
                    timestamp,
                    h.getTemperature_2m().get(i),
                    h.getWind_speed_10m().get(i),
                    h.getShortwave_radiation().get(i),   // ☀ irradiance
                    h.getPrecipitation().get(i),      // 🌧 rain
                    h.getCloudcover().get(i) / 100.0   // 0..1
            ));
        }

        return new WeatherTimeSeries(points);
    }
}
