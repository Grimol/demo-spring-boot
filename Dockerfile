# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copier uniquement les fichiers de build pour tirer parti du cache
COPY pom.xml .
# Si tu as un répertoire .mvn/ et mvnw, décommente ces lignes :
COPY .mvn .mvn
COPY mvnw mvnw
RUN mvn -B -ntp -q -DskipTests dependency:go-offline

# Copier le code et builder
COPY src ./src
# Astuce : on fixe le nom du jar sorti pour simplifier la copie
RUN mvn -B -ntp -DskipTests -Dproject.build.finalName=app package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Créer le répertoire pour SQLite
RUN mkdir -p /app/data

# Sécurité : exécuter avec un user non-root
RUN addgroup -S spring && adduser -S spring -G spring && \
    chown -R spring:spring /app/data
USER spring:spring

# Optionnel : variables pour tuning JVM
ENV JAVA_OPTS=""

# Copier le jar
ARG JAR_FILE=/app/target/*.jar
COPY --from=build ${JAR_FILE} /app/app.jar

EXPOSE 8080
# HEALTHCHECK --interval=30s --timeout=3s --start-period=20s CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Démarrage
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
