# Stage 1: Build the Spring Boot app
FROM maven:3-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests

# Stage 2: Run the Spring Boot app
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/todo-list-api-0.0.1-SNAPSHOT.jar app.jar

# Create a folder for SQLite persistent data
RUN mkdir -p /app/data

# Set environment variables
# Point Spring Boot to the SQLite database inside the persistent folder
ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/app/data/todos.db
ENV SPRING_PROFILES_ACTIVE=docker

# Expose the default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]