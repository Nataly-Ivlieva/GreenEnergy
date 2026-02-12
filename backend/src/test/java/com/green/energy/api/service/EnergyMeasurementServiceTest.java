package com.green.energy.api.service;

import com.green.energy.api.domain.AnomalyType;
import com.green.energy.api.entity.EnergyMeasurementEntity;
import com.green.energy.api.entity.GeneratorEntity;
import com.green.energy.api.repository.EnergyMeasurementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnergyMeasurementServiceTest {

    @Mock
    private EnergyMeasurementRepository repository;

    @Mock
    private GeneratorStatusService generatorStatusService;

    @InjectMocks
    private EnergyMeasurementService service;

    @Test
    void getChartForGenerator_shouldMapAndDetectAnomaly() {

        UUID generatorId = UUID.randomUUID();

        GeneratorEntity generator = new GeneratorEntity();
        generator.setId(generatorId);

        EnergyMeasurementEntity entity = new EnergyMeasurementEntity();
        entity.setGenerator(generator);
        entity.setActualPowerKw(50.0);
        entity.setExpectedPowerKw(100.0);
        entity.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));

        when(repository.findLatestByGenerator(generatorId))
                .thenReturn(List.of(entity));

        when(generatorStatusService.detect(50.0, 100.0))
                .thenReturn(
                        new GeneratorStatusService.AnomalyResult(
                                true,
                                AnomalyType.CRITICAL
                        )
                );

        var result = service.getChartForGenerator(generatorId);

        assertEquals(1, result.size());
        assertTrue(result.get(0).anomalous());
        verify(repository).findLatestByGenerator(generatorId);
    }
}

