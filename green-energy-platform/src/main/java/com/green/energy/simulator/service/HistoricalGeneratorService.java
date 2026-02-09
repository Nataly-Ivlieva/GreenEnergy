package com.green.energy.simulator.service;

import com.green.energy.simulation.power.PowerOutput;
import com.green.energy.simulation.service.GeneratorService;
import com.green.energy.simulation.weather.WeatherSnapshot;

import com.green.energy.simulator.config.GeneratorProperties;
import com.green.energy.simulator.event.HistoricalEvent;
import com.green.energy.simulator.generator.GeneratorRepository;
import com.green.energy.simulator.model.Generator;

import com.green.energy.simulator.weather.OpenMeteoClient;
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
    private final JsonEventWriter writer;
    private final GeneratorProperties props;
    private final GeneratorService generatorService;

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

            for (WeatherSnapshot w : weather.getPoints()) {
                PowerOutput finalPower = generatorService.generate(w, g.getType(), g.getNominalPowerKw());
                Instant timestamp = w.timestamp();
                HistoricalEvent event = HistoricalEvent.from(g, w, finalPower, timestamp);

                writer.write(event);
            }
        }

        writer.close();
    }
}
