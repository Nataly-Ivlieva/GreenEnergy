package com.green.energy.simulator.weather;

import com.green.energy.simulation.weather.WeatherSnapshot;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class WeatherMapper {

    public static WeatherTimeSeries map(OpenMeteoResponse response) {
        List<WeatherSnapshot> points = new ArrayList<>();
        var h = response.getHourly();

        for (int i = 0; i < h.getTime().size(); i++) {
            String raw = h.getTime().get(i);

            Instant timestamp =
                    raw.length() == 16
                            ? Instant.parse(raw + ":00Z")
                            : Instant.parse(raw + "Z");
            points.add(new WeatherSnapshot(
                    timestamp,
                    h.getTemperature_2m().get(i),
                    h.getWind_speed_10m().get(i),
                    h.getShortwave_radiation().get(i),
                    h.getPrecipitation().get(i),
                    h.getCloudcover().get(i) / 100.0
            ));
        }

        return new WeatherTimeSeries(points);
    }
}
