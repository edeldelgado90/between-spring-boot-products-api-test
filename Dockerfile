# Use an image for Maven and amazoncorretto 21
FROM maven:3.9.9-amazoncorretto-21 AS build

# Worker directory in the container
WORKDIR /app

# Copy the pom.xml and the source code
COPY pom.xml ./
COPY src ./src

# Build the application
RUN mvn clean install -U

# Copy the generated jar to the container
COPY target/*.jar app.jar

# Expose the ports for HTTP
EXPOSE 8080 9090

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
