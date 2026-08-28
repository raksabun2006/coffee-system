# Stage 1: Build stage
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /build

# Cache Gradle wrapper & build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || true

# Build application
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# Extract Spring Boot layers
RUN java -Djarmode=layertools -jar build/libs/*.jar extract --destination extracted

# Stage 2: Runtime image
FROM eclipse-temurin:25-jre
WORKDIR /app

# Non-root user setup
RUN useradd -u 1001 -r -g root -s /sbin/nologin appuser
USER appuser

# Copy extracted layers
COPY --from=builder /build/extracted/dependencies/ ./
COPY --from=builder /build/extracted/spring-boot-loader/ ./
COPY --from=builder /build/extracted/snapshot-dependencies/ ./
COPY --from=builder /build/extracted/application/ ./

EXPOSE 8098

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]