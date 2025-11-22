# ========================================
# STAGE 1: BUILD
# ========================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradle/ gradle/
COPY gradlew gradlew.bat settings.gradle ./
COPY build.gradle ./

RUN ./gradlew dependencies --no-daemon

COPY src/ src/

RUN ./gradlew bootJar --no-daemon -x test

# ========================================
# STAGE 2: RUNTIME
# ========================================
FROM eclipse-temurin:21-jre-jammy

LABEL maintainer="GreenThumb Team"
LABEL description="Backend Spring Boot pour GreenThumb"
LABEL version="0.1.0"

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

# Crée uniquement le dossier logs (pas uploads, le volume s'en occupe)
RUN mkdir -p logs

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENV SPRING_PROFILES_ACTIVE=dev \
    JAVA_OPTS="-Xms512m -Xmx1024m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]