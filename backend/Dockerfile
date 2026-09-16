# Multi-stage build for SIGEA-GTT Spring Boot with Java 21 LTS
FROM maven:3.9.8-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build production jar
COPY src ./src
RUN mvn clean package -DskipTests -B

# Minimal runtime stage with Eclipse Temurin 21 JRE Alpine
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-privileged user for clinical-grade security
RUN addgroup -S sigea && adduser -S sigea -G sigea
USER sigea:sigea

# Copy compiled jar from builder
COPY --from=builder /build/target/*.jar app.jar

# Expose default port
EXPOSE 8080

# Environment variables with sensible cloud defaults
ENV PORT=8080
ENV JAVA_TOOL_OPTIONS="-XX:+UseSerialGC -Xss512k -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"

# Entry point execution
ENTRYPOINT ["java", "-jar", "app.jar"]
