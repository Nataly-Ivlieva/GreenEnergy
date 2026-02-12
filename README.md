Green Energy Real-Time Data Platform
Project Overview

This project implements a scalable and maintainable backend architecture for a data-intensive real-time reporting application in the domain of renewable energy. The primary focus lies on data engineering principles such as reliability, scalability, maintainability, and reproducibility.

The system continuously processes time-series data originating from external weather APIs as well as simulated renewable energy generators (solar, wind, hydro). Each data point contains a timestamp, and the overall data volume exceeds one million records, making the platform suitable for large-scale batch and real-time processing scenarios.

Architecture Overview

The architecture follows a microservice-oriented design and is fully containerized using Docker. Each component is isolated and independently deployable, ensuring fault tolerance and ease of maintenance.

The system consists of the following main components:

Data Generator & Ingestion Service (Java, Spring Boot)
Responsible for collecting raw data from external weather APIs and simulated energy sources. The service normalizes incoming data and forwards it to downstream processing components.

Processing & Aggregation Service
Performs data preprocessing, time-based windowing, and aggregations required for real-time reporting. This service also integrates online inference using a pre-trained machine learning model.

Time-Series Data Storage
A persistent storage layer designed to store both real-time aggregated data and historical raw data. Historical data is used for offline analytics and model training.

Machine Learning Module (Python)
A standalone offline training pipeline that processes historical data, trains a predictive model, and exports the trained model for use in the real-time processing service.

Serving Layer (REST API)
Provides read-only access to aggregated real-time and historical data. This API serves as the interface for external consumers.

Frontend (Read-Only, Map-Based Visualization)
A lightweight frontend application that visualizes aggregated renewable energy data on a geographic map using the REST API.

How to Run the Services

The core microservices are containerized using Docker. Each service can be built and started independently.

1️ Backend Service (Java, Spring Boot)
docker build -t backend .
docker run -p 8081:8080 backend

The backend will be available at:
http://localhost:8081

2️ Data Generator & Ingestion Service
docker build -t generator-simulator .
docker run -p 8082:8080 generator-simulator

The service will be available at:
http://localhost:8082

3️ Machine Learning Model API (Python)
docker build -t energy-api -f ml/api/Dockerfile .
docker run -p 8000:8000 -e ENERGY_API_KEY=test-key energy-api

The Model API will be available at:
http://localhost:8000

Notes

Ensure Docker is installed and running.

Services may require environment variables (e.g., API keys or database credentials).

The ML API requires the ENERGY_API_KEY environment variable.

Make sure that the database service is running before starting dependent services.
