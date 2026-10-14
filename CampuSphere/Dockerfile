# ============================================================
# CampuSphere Backend — Multi-stage Dockerfile
# ============================================================

# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper and pom first (layer caching)
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Ensure the Maven wrapper is executable (file mode may be lost when committed from Windows)
RUN chmod +x mvnw

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -q

# Copy source and build
COPY src src
RUN ./mvnw package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Add non-root user for security
RUN addgroup -S campusphere && adduser -S campusphere -G campusphere
USER campusphere

# Copy built JAR
COPY --from=build /app/target/*.jar app.jar

# Expose default port (Railway overrides via PORT env var at runtime)
EXPOSE 8080

# Health check — uses PORT env var with 8080 as fallback
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:${PORT:-8080}/actuator/health || exit 1

# Pass PORT to the JVM so Spring Boot picks it up via ${PORT:8080}
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]
