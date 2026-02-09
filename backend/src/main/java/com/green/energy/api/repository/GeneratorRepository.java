package com.green.energy.api.repository;

import com.green.energy.api.entity.GeneratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GeneratorRepository extends JpaRepository<GeneratorEntity, UUID> {
}

