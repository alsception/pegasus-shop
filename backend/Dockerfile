# Use an official Java 21 runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/pegasus-0.0.1-SNAPSHOT.jar /app/pegasus-0.0.1-SNAPSHOT.jar

# Expose the port the app runs on (default is 8080 for Spring Boot)
EXPOSE 8080

# Run the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "pegasus-0.0.1-SNAPSHOT.jar"]

