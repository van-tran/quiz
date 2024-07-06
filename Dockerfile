# Use a base image with JDK 11 or later
FROM amazoncorretto:17-alpine

# Install curl
RUN apk --no-cache add curl

# Set the working directory in the Docker container
WORKDIR /app

# Copy the built JAR file into the Docker image
COPY build/libs/QuizScore-0.0.1-SNAPSHOT.jar /app/QuizScore-0.0.1-SNAPSHOT.jar

# Expose the port the application runs on
EXPOSE 8080



# Command to run the application
ENTRYPOINT ["java","-jar","/app/QuizScore-0.0.1-SNAPSHOT.jar"]