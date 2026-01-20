package com.green.energy.simulator.service;
import com.green.energy.simulator.anomaly.AnomalyInjector;
import com.green.energy.simulator.config.GeneratorProperties;
import com.green.energy.simulator.event.HistoricalEvent;
import com.green.energy.simulator.model.Generator;
import com.green.energy.simulator.generator.GeneratorRepository;
import com.green.energy.simulator.power.PowerModel;
import com.green.energy.simulator.power.PowerOutput;
import com.green.energy.simulator.weather.OpenMeteoClient;
import com.green.energy.simulator.weather.WeatherDataPoint;
import com.green.energy.simulator.weather.WeatherTimeSeries;
import com.green.energy.simulator.writer.JsonEventWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricalGeneratorService {

    private final GeneratorRepository generatorRepository;
    private final OpenMeteoClient weatherClient;
    private final PowerModel powerModel;
    private final AnomalyInjector anomalyInjector;
    private final JsonEventWriter writer;
    private final GeneratorProperties props;

    public void generate() throws IOException {

        List<Generator> generators =
                generatorRepository.load(props.getGenerators());

        for (Generator g : generators) {

            WeatherTimeSeries weather =
                    weatherClient.fetchHourlyWeather(
                            g.getLatitude(),
                            g.getLongitude(),
                            props.getStartDate(),
                            props.getEndDate()
                    );

            for (WeatherDataPoint w : weather.getPoints()) {

                Instant timestamp = w.getTimestamp();

                PowerOutput normal = powerModel.calculate(g, w);
                PowerOutput power = anomalyInjector.injectIfNeeded(normal, g.getType());

                HistoricalEvent event =
                        HistoricalEvent.from(g, w, power, timestamp);

                writer.write(event);
            }
        }

        writer.close();
    }
}
