# ==========================================
# Stage 1: Build the Spring Boot application
# ==========================================
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build

# 1. Copy pom.xml and cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copy source code and build the executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Minimal Production Runtime
# ==========================================
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy the built JAR from the build stage as app.jar
COPY --from=build /build/target/book-network-api*.jar app.jar

# Expose backend port
EXPOSE 8080

# Configure Spring Boot environment defaults
ENV SPRING_PROFILES_ACTIVE=dev
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-sql-bsn:5432/book_social_network

# Run the application with exec form for graceful shutdown support
ENTRYPOINT ["java", "-jar", "app.jar"]