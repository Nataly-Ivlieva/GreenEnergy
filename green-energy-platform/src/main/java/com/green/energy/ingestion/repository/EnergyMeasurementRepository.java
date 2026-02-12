package com.green.energy.ingestion.repository;

import com.green.energy.ingestion.entity.EnergyMeasurementEntity;
import com.green.energy.ingestion.entity.GeneratorEntity;
import com.green.energy.ingestion.model.GeneratorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnergyMeasurementRepository
        extends JpaRepository<EnergyMeasurementEntity, Long> {

    @Query("""
        select em from EnergyMeasurementEntity em
        where em.generator.id = :generatorId
        order by em.timestamp desc
        limit 1
    """)
    Optional<EnergyMeasurementEntity> findLatestByGenerator(UUID generatorId);

    @Query("""
        select e from EnergyMeasurementEntity e
        where e.timestamp between :from and :to
          and (:type is null or e.generator.type = :type)
        order by e.timestamp limit 100
    """)
    List<EnergyMeasurementEntity> findForChart(
            OffsetDateTime from,
            OffsetDateTime to,
            GeneratorType type
    );

    EnergyMeasurementEntity findTopByGeneratorOrderByTimestampDesc(
            GeneratorEntity generator
    );

    @Query("""
        select max(e.timestamp)
        from EnergyMeasurementEntity e
    """)
    OffsetDateTime findMaxTimestamp();
}