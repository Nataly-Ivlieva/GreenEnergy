package com.green.energy.ingestion.repository;

import com.green.energy.ingestion.entity.GeneratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GeneratorRepository extends JpaRepository<GeneratorEntity, UUID> {
}

