# ========================================
# STAGE 1: BUILD
# ========================================
# Utilise une image avec JDK 21 pour compiler l'application
FROM eclipse-temurin:21-jdk-alpine AS builder

# Définit le répertoire de travail
WORKDIR /app

# Copie les fichiers de configuration Gradle
# (Ces fichiers changent rarement, donc Docker peut mettre en cache cette layer)
COPY gradle/ gradle/
COPY gradlew gradlew.bat settings.gradle ./
COPY build.gradle ./

# Télécharge les dépendances (mise en cache si build.gradle n'a pas changé)
RUN ./gradlew dependencies --no-daemon

# Copie le code source
COPY src/ src/

# Compile l'application et crée le JAR exécutable
# --no-daemon: évite de laisser un daemon Gradle en mémoire
# -x test: skip les tests pour un build plus rapide (à ajuster selon vos besoins)
RUN ./gradlew bootJar --no-daemon -x test

# ========================================
# STAGE 2: RUNTIME
# ========================================
# Utilise une image légère avec uniquement le JRE 21
FROM eclipse-temurin:21-jre-jammy

# Informations sur l'image
LABEL maintainer="GreenThumb Team"
LABEL description="Backend Spring Boot pour GreenThumb"
LABEL version="0.1.0"

# Crée un utilisateur non-root pour des raisons de sécurité
RUN groupadd -r spring && useradd -r -g spring spring

# Définit le répertoire de travail
WORKDIR /app

# Copie le JAR depuis le stage de build
COPY --from=builder /app/build/libs/*.jar app.jar

# Crée les répertoires nécessaires avec les bonnes permissions
RUN mkdir -p logs uploads/users && \
    chown -R spring:spring logs uploads app.jar

# Passe à l'utilisateur non-root
USER spring:spring

# Expose le port 8080 (port par défaut de Spring Boot)
EXPOSE 8080

# Point de santé pour les orchestrateurs (Portainer, Kubernetes, etc.)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Variables d'environnement par défaut (peuvent être surchargées)
ENV SPRING_PROFILES_ACTIVE=dev \
    JAVA_OPTS="-Xms512m -Xmx1024m"

# Commande de démarrage de l'application
# Les variables d'environnement seront injectées au runtime
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]