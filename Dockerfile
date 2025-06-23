FROM maven:3.8.7-openjdk-18-slim

WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean compile

EXPOSE 3000

CMD ["mvn", "spring-boot:run"] 