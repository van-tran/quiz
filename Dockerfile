# Use a base image with JDK 11 or later
FROM openjdk:11-jre-slim

# Set the working directory in the Docker container
WORKDIR /app

# Copy the built JAR file into the Docker image
COPY build/libs/vocabulary-quiz-scoring-service-0.0.1-SNAPSHOT.jar /app/vocabulary-quiz-scoring-service.jar

# Expose the port the application runs on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "/app/vocabulary-quiz-scoring-service.jar"]