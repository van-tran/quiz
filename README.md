# Vocabulary Quiz Scoring Service

## Overview
The Vocabulary Quiz Scoring Service is a Spring Boot application designed to process quiz submissions, score them, and manage the results using Kafka for messaging and Redis for data storage. This service listens for quiz submissions on a Kafka topic, scores the submissions, saves the scores in Redis, and then publishes the results back to another Kafka topic.

## Features
- **Quiz Submission Processing**: Listens for quiz submissions on a Kafka topic.
- **Scoring Mechanism**: Implements a simple scoring algorithm based on the quiz answers.
- **Result Management**: Saves quiz scores in Redis and publishes the scored results to a Kafka topic.

## Technologies
- Kotlin
- Spring Boot
- Gradle
- Kafka : Middleware for communicating between services
- Redis : In-memory data store that's used for storing quiz scores & leaderboard

## Getting Started

### Prerequisites
- JDK 11 or later
- Docker and Docker Compose
- Gradle

### Testing the Application
1. Run the tests using gradle tasks:
2. `./gradlew test`

### Running the Application
1. Environment variables in .env file
2. Start docker-compose to run Kafka, MongoDB and Redis
3. Run the application