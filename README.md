Green Energy Real-Time Data Platform
Project Overview

This project implements a scalable, containerized microservice architecture for a data-intensive real-time reporting platform in the renewable energy domain.

The primary focus lies on data engineering principles such as:

reliability

scalability

maintainability

reproducibility

separation of concerns

The platform continuously processes time-series data originating from:

simulated renewable energy generators (solar, wind, hydro)

external weather data sources

machine learning inference results

Each data point contains a timestamp, and the overall data volume exceeds one million records, making the system suitable for large-scale batch and near real-time processing scenarios.

Architecture Overview

The system follows a microservice-oriented architecture and is fully containerized using Docker and Docker Compose.

Each component runs in an isolated container and communicates via internal Docker networking. This ensures:

fault isolation

independent deployment

service discoverability via internal DNS

simplified local reproducibility

System Components
Data Generator & Ingestion Service

(Java, Spring Boot)

Responsible for:

generating simulated renewable energy data

integrating weather-based features

normalizing and validating incoming data

writing historical datasets

calling the ML inference API

This service operates in scheduled intervals and simulates large-scale time-series generation.

Default port: 8082

Processing & Backend Service

(Java, Spring Boot)

Responsible for:

persistence of processed data

database interaction (PostgreSQL)

time-based aggregations

exposing reporting endpoints

This service acts as the central backend layer.

Default port: 8081

Time-Series Data Storage

(PostgreSQL)

Used for:

real-time aggregated data

historical raw data

persistence across container restarts

The database runs in its own container and is accessed via internal Docker networking.

Machine Learning Module

(Python)

Consists of two parts:

Offline training pipeline

Online inference API (FastAPI + Uvicorn)

The ML API:

loads a pre-trained model

exposes prediction endpoints

provides model status checks

Default port: 8000

Serving Layer (REST API)

The backend service exposes REST endpoints for:

aggregated energy production

historical datasets

model-enriched data

The API is read-only and designed for external consumers.

Frontend (Optional)

A lightweight read-only frontend can consume the REST API and visualize:

energy production

geographic distributions

time-based trends

How to Run the Platform (Recommended Way)

The system is designed to run using Docker Compose, which starts all services together with proper networking.

Start the entire system

From the project root:

docker compose up --build

This will start:

PostgreSQL

Backend service

Generator & ingestion service

ML API

All services will communicate via internal Docker networking.

Stop the system
docker compose down

To remove volumes:

docker compose down -v

Service Endpoints (Local)
Service URL
Backend API http://localhost:8081

Generator Service http://localhost:8082

ML API http://localhost:8000
Environment Configuration

The services rely on environment variables configured via Docker Compose:

SPRING_DATASOURCE_URL

SPRING_DATASOURCE_USERNAME

SPRING_DATASOURCE_PASSWORD

INTERSERVICE_ML_BASE_URL

Database hostname inside Docker:

postgres

ML API hostname inside Docker:

ml-api

Design Principles Applied

Microservice separation

Containerized deployment

Service-to-service communication via internal DNS

Clear responsibility boundaries

Offline + online ML workflow separation

Reproducible local environment via Docker Compose

Notes

Docker must be installed and running.

Ports 8000, 8081, 8082, and 5432 must be available.

The ML API must be reachable before the generator performs inference.

PostgreSQL starts automatically via Docker Compose.
