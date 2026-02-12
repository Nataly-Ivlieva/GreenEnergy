package com.green.energy.ingestion.service;

import com.green.energy.ingestion.entity.EnergyMeasurementEntity;
import com.green.energy.ingestion.entity.GeneratorEntity;
import com.green.energy.ingestion.entity.WeatherDataEmbeddable;
import com.green.energy.ingestion.ml.MlClient;
import com.green.energy.ingestion.model.GeneratorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PredictionServiceTest {

    @Mock
    private MlClient mlClient;

    @InjectMocks
    private PredictionService service;

    @Test
    void predictBatch_shouldReturnEmptyList_whenInputNull() {
        var result = service.predictBatch(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void predictBatch_shouldReturnEmptyList_whenInputEmpty() {
        var result = service.predictBatch(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void predictBatch_shouldReturnMlResult_whenSuccessful() {

        EnergyMeasurementEntity measurement = mock(EnergyMeasurementEntity.class);
        GeneratorEntity generator = mock(GeneratorEntity.class);
        WeatherDataEmbeddable weather = mock(WeatherDataEmbeddable.class);

        when(measurement.getTimestamp()).thenReturn(OffsetDateTime.now(ZoneOffset.UTC));
        when(measurement.getGenerator()).thenReturn(generator);
        when(measurement.getWeather()).thenReturn(weather);

        when(generator.getType()).thenReturn(GeneratorType.SOLAR);
        when(generator.getMaxCapacityKw()).thenReturn(100.0);

        when(weather.getTemperatureC()).thenReturn(20.0);
        when(weather.getWindSpeedMs()).thenReturn(5.0);
        when(weather.getSolarIrradianceWm2()).thenReturn(500.0);
        when(weather.getPrecipitationMm()).thenReturn(0.0);
        when(weather.getCloudCover()).thenReturn(20.0);

        when(mlClient.predictExpectedPowerBatch(any()))
                .thenReturn(List.of(80.0));

        var result = service.predictBatch(List.of(measurement));

        assertEquals(1, result.size());
        assertEquals(80.0, result.get(0));
    }

    @Test
    void predictBatch_shouldReturnZeros_whenMlFails() {

        EnergyMeasurementEntity measurement = mock(EnergyMeasurementEntity.class);
        GeneratorEntity generator = mock(GeneratorEntity.class);
        WeatherDataEmbeddable weather = mock(WeatherDataEmbeddable.class);

        when(measurement.getTimestamp()).thenReturn(OffsetDateTime.now(ZoneOffset.UTC));
        when(measurement.getGenerator()).thenReturn(generator);
        when(measurement.getWeather()).thenReturn(weather);

        when(generator.getType()).thenReturn(GeneratorType.SOLAR);
        when(generator.getMaxCapacityKw()).thenReturn(100.0);

        when(weather.getTemperatureC()).thenReturn(20.0);
        when(weather.getWindSpeedMs()).thenReturn(5.0);
        when(weather.getSolarIrradianceWm2()).thenReturn(500.0);
        when(weather.getPrecipitationMm()).thenReturn(0.0);
        when(weather.getCloudCover()).thenReturn(20.0);

        when(mlClient.predictExpectedPowerBatch(any()))
                .thenThrow(new RuntimeException());

        var result = service.predictBatch(List.of(measurement));

        assertEquals(1, result.size());
        assertEquals(0.0, result.get(0));
    }
}
