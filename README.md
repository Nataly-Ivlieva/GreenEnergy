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
