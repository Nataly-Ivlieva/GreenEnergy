package com.green.energy.api.entity;

import com.green.energy.api.domain.GeneratorType;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "generators")
public class GeneratorEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GeneratorType type;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private double maxCapacityKw;

    private String name;

    public UUID getId() {
        return id;
    }

    public GeneratorType getType() {
        return type;
    }

    public void setType(GeneratorType type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getMaxCapacityKw() {
        return maxCapacityKw;
    }

    public void setMaxCapacityKw(double maxCapacityKw) {
        this.maxCapacityKw = maxCapacityKw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}

