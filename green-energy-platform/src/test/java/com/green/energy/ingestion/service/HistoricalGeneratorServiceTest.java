package com.green.energy.ingestion.service;

import com.green.energy.ingestion.model.GeneratorType;
import com.green.energy.ingestion.power.PowerOutput;
import com.green.energy.ingestion.weather.WeatherSnapshot;
import com.green.energy.ingestion.weather.WeatherTimeSeries;
import com.green.energy.ingestion.config.GeneratorProperties;
import com.green.energy.ingestion.event.HistoricalEvent;
import com.green.energy.ingestion.model.Generator;
import com.green.energy.ingestion.repository.GeneratorRandomRepository;
import com.green.energy.ingestion.weather.OpenMeteoClient;
import com.green.energy.ingestion.writer.JsonEventWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoricalGeneratorServiceTest {

    @Mock
    private GeneratorRandomRepository generatorRepository;

    @Mock
    private OpenMeteoClient weatherClient;

    @Mock
    private JsonEventWriter writer;

    @Mock
    private GeneratorProperties props;

    @Mock
    private GeneratorService generatorService;

    @InjectMocks
    private HistoricalGeneratorService service;

    @Test
    void generate_shouldProcessGeneratorsAndWriteEvents() throws Exception {

        // generator
        Generator generator = mock(Generator.class);
        when(generator.getLatitude()).thenReturn(50.0);
        when(generator.getLongitude()).thenReturn(10.0);
        when(generator.getType()).thenReturn(GeneratorType.SOLAR);
        when(generator.getNominalPowerKw()).thenReturn(100.0);

        when(props.getGenerators()).thenReturn(1);
        when(props.getStartDate()).thenReturn(LocalDate.now().minusDays(1));
        when(props.getEndDate()).thenReturn(LocalDate.now());

        when(generatorRepository.load(anyInt()))
                .thenReturn(List.of(generator));

        // weather
        WeatherSnapshot snapshot = mock(WeatherSnapshot.class);
        when(snapshot.timestamp()).thenReturn(Instant.from(OffsetDateTime.now(ZoneOffset.UTC)));

        WeatherTimeSeries series = mock(WeatherTimeSeries.class);
        when(series.getPoints()).thenReturn(List.of(snapshot));

        when(weatherClient.fetchHourlyWeather(
                anyDouble(),
                anyDouble(),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(series);

        PowerOutput output = mock(PowerOutput.class);
        when(generatorService.generate(any(), any(), anyDouble()))
                .thenReturn(output);

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            filesMock.when(() -> Files.exists(JsonEventWriter.OUTPUT))
                    .thenReturn(false);

            service.generate();

            verify(generatorRepository).load(anyInt());
            verify(weatherClient).fetchHourlyWeather(anyDouble(), anyDouble(), any(), any());
            verify(generatorService).generate(any(), any(), anyDouble());
            verify(writer).write(any(HistoricalEvent.class));
            verify(writer).close();
        }
    }

    @Test
    void generate_shouldReturnImmediately_ifFileExists() throws Exception {

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            filesMock.when(() -> Files.exists(JsonEventWriter.OUTPUT))
                    .thenReturn(true);

            service.generate();

            verifyNoInteractions(generatorRepository);
            verifyNoInteractions(weatherClient);
            verifyNoInteractions(writer);
        }
    }
}

