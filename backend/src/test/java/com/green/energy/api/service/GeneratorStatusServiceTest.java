package com.green.energy.api.service;

import com.green.energy.api.domain.AnomalyType;
import com.green.energy.api.domain.GeneratorType;
import com.green.energy.api.entity.EnergyMeasurementEntity;
import com.green.energy.api.entity.GeneratorEntity;
import com.green.energy.api.repository.EnergyMeasurementRepository;
import com.green.energy.api.repository.GeneratorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeneratorStatusServiceTest {

    @Mock
    private GeneratorRepository generatorRepository;

    @Mock
    private EnergyMeasurementRepository measurementRepository;

    @InjectMocks
    private GeneratorStatusService service;

    @Test
    void detect_shouldReturnCritical_whenErrorMoreThan30Percent() {
        var result = service.detect(50.0, 100.0);

        assertTrue(result.anomalous());
        assertEquals(AnomalyType.CRITICAL, result.type());
    }

    @Test
    void detect_shouldReturnWarning_whenErrorMoreThan15Percent() {
        var result = service.detect(80.0, 100.0);

        assertTrue(result.anomalous());
        assertEquals(AnomalyType.WARNING, result.type());
    }

    @Test
    void detect_shouldReturnNormal_whenWithin15Percent() {
        var result = service.detect(95.0, 100.0);

        assertFalse(result.anomalous());
        assertNull(result.type());
    }

    @Test
    void getCurrentStatus_shouldReturnMappedStatuses() {
        GeneratorEntity generator = new GeneratorEntity();
        generator.setId(UUID.randomUUID());
        generator.setName("Solar-1");
        generator.setType(GeneratorType.SOLAR);
        generator.setLatitude(10.0);
        generator.setLongitude(20.0);

        EnergyMeasurementEntity measurement = new EnergyMeasurementEntity();
        measurement.setActualPowerKw(50.0);
        measurement.setExpectedPowerKw(100.0);
        measurement.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        measurement.setGenerator(generator);

        when(generatorRepository.findAll()).thenReturn(List.of(generator));
        when(measurementRepository
                .findTopByGeneratorOrderByTimestampDesc(generator))
                .thenReturn(measurement);

        var result = service.getCurrentStatus();

        assertEquals(1, result.size());
        assertTrue(result.get(0).anomalous());
        verify(generatorRepository).findAll();
    }
}

