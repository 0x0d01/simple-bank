# Build stage
FROM maven:3.8.7-openjdk-18-slim AS build

WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application and create JAR
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:18-ea-8-jdk-slim

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/simple-bank-0.0.1-SNAPSHOT.jar app.jar

# Copy the keys directory and verify it's copied
COPY keys ./keys

EXPOSE 3000

# Run the JAR file
CMD ["java", "-jar", "app.jar"] 